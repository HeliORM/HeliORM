package com.heliorm.mojo.annotated;

import com.heliorm.Database;
import com.heliorm.Table;
import com.heliorm.annotation.Ignore;
import com.heliorm.annotation.Pojo;
import com.heliorm.def.Field;
import com.heliorm.def.Index;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of Table that is build from POJO annotations.
 *
 * @author gideon
 */
public final class AnnotatedPojoTable implements Table {

    private final Database database;
    private final Class<?> pojoClass;
    private List<Field> fieldModels;
    private List<Index> indexes;
    private final Set<Table> subs;

    /**
     * Create a new table with the given database, POJO class and set of
     * sub-tables.
     *
     * @param database The database
     * @param pojoClass The POJO class
     * @param subTables The set of sub-tables
     */
    public AnnotatedPojoTable(Database database, Class<?> pojoClass, Set<AnnotatedPojoTable> subTables) {
        this.database = database;
        this.pojoClass = pojoClass;
        this.subs = unrollSubTables(subTables);
    }

    @Override
    public String getSqlTable() {
        Optional<Pojo> pojo = getAnnotation(Pojo.class);
        if (pojo.isPresent()) {
            if (!pojo.get().tableName().isEmpty()) {
                return pojo.get().tableName();
            }
        }
        return getJavaName();
    }

    @Override
    public List<Field> getFields() {
        if (fieldModels == null) {
            List<java.lang.reflect.Field> fields = getAllFields(pojoClass);
            fieldModels = new ArrayList();
            for (java.lang.reflect.Field field : fields) {
                if (isDataField(field)) {
                    fieldModels.add(new AnnotatedPojoField(this, field));
                }
            }
        }
        return fieldModels;
    }

    @Override
    public Optional<Field> getPrimaryKey() {
        return getFields().stream().filter(field -> field.isPrimaryKey()).findAny();
    }

    @Override
    public Database getDatabase() {
        return database;
    }

    @Override
    public Class getObjectClass() {
        return pojoClass;
    }

    @Override
    public Set<Table> getSubTables() {
        return new HashSet(subs);
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(pojoClass.getModifiers());
    }

    @Override
    public List<Index> getIndexes() {
        if (indexes == null) {
            indexes = new ArrayList<>();
            com.heliorm.annotation.Index[] anns = pojoClass.getAnnotationsByType(com.heliorm.annotation.Index.class);
            for (com.heliorm.annotation.Index ann : anns) {
                indexes.add(new AnnotatedPojoIndex(this, ann));
            }
        }
        return indexes;
    }

    /**
     * Decide if a reflected field is a data field we need to process. A field
     * is considered a POJO field if it is not native, not transient, not static
     * and not annotated with the @Ignore annotation.
     *
     *
     * @param field The reflected field to evaluate
     * @return True if it is considered a data field
     */
    private boolean isDataField(java.lang.reflect.Field field) {
        int modifiers = field.getModifiers();
        if (Modifier.isNative(modifiers)) {
            return false;
        }
        if (Modifier.isTransient(modifiers)) {
            return false;
        }
        if (Modifier.isStatic(modifiers)) {
            return false;
        }
        return !field.isAnnotationPresent(Ignore.class);
    }

    /**
     * Return the Java name for the POJO class represented by this
     *
     * @return The Java name
     */
    private String getJavaName() {
        return pojoClass.getSimpleName();
    }

    /**
     * Convenience method to find the optional annotation on the POJO class.
     *
     * @param <T> The type of the annotation
     * @param annotationClass The annotation class
     * @return Optional annotation found
     */
    private <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationClass) {
        return Optional.ofNullable(pojoClass.getAnnotation(annotationClass));
    }

    /**
     * Return reflected fields for all fields declared on a class and it's super
     * classes but stopping short of java.lang.Object (recursively)
     *
     * @param clazz The class to return the fields from
     * @return The list of fields
     */
    private List<java.lang.reflect.Field> getAllFields(Class<?> clazz) {
        List<java.lang.reflect.Field> res = new LinkedList();
        if (clazz.getSuperclass() != Object.class) {
            res.addAll(getAllFields(clazz.getSuperclass()));
        }
        res.addAll(Arrays.asList(clazz.getDeclaredFields()));
        return res;
    }

    private Set<Table> unrollSubTables(Set<AnnotatedPojoTable> subs) {
        Set<Table> res = new HashSet();
        for (Table sub : subs) {
            if (!sub.isAbstract()) {
                res.add(sub);
            }
            res.addAll(unrollSubTables(sub.getSubTables()));
        }
        return res;
    }

}
