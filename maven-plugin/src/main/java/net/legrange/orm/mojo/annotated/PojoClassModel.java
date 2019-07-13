package net.legrange.orm.mojo.annotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.legrange.orm.Database;
import net.legrange.orm.Table;
import net.legrange.orm.annotation.Ignore;
import net.legrange.orm.annotation.Pojo;
import net.legrange.orm.def.Field;

/**
 *
 * @author gideon
 */
public class PojoClassModel implements Table {

    private final Database database;
    private final Class<?> pojoClass;
    private List<Field> fieldModels;
    private final Set<PojoClassModel> subs;

    public PojoClassModel(Database database, Class<?> pojoClass, Set<PojoClassModel> subTables) {
        this.database = database;
        this.pojoClass = pojoClass;
        this.subs = subTables;
    }

    private String getJavaName() {
        return pojoClass.getSimpleName();
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
                    fieldModels.add(new PojoFieldModel(field));
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
        if (field.isAnnotationPresent(Ignore.class)) {
            return false;
        }
        return true;
    }

    private <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationClass) {
        return Optional.ofNullable(pojoClass.getAnnotation(annotationClass));
    }

    private List<java.lang.reflect.Field> getAllFields(Class<?> clazz) {
        List<java.lang.reflect.Field> res = new LinkedList();
        if (clazz.getSuperclass() != Object.class) {
            res.addAll(getAllFields(clazz.getSuperclass()));
        }
        res.addAll(Arrays.asList(clazz.getDeclaredFields()));
        return res;
    }

    @Override
    public Class getObjectClass() {
        return pojoClass;
    }

    @Override
    public Set<Table> getSubTables() {
        return new HashSet(subs);
    }

}
