package com.heliorm.mojo.annotated;

import com.heliorm.Database;
import com.heliorm.Field;
import com.heliorm.annotation.Ignore;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Implementation of Table that is build from POJO annotations.
 *
 * @author gideon
 */
public final class AnnotatedPojoClassTable extends AnnotatedPojoTable {

    private List<Field> fieldModels;

    /**
     * Create a new table with the given database, POJO class and set of
     * sub-tables.
     *
     * @param database  The database
     * @param pojoClass The POJO class
     * @param subTables The set of sub-tables
     */
    public AnnotatedPojoClassTable(Database database, Class<?> pojoClass, Set<AnnotatedPojoTable> subTables) {
        super(database, pojoClass, subTables);
    }

    @Override
    public List<Field> getFields() {
        if (fieldModels == null) {
            List<java.lang.reflect.Field> fields = getAllFields(getObjectClass());
            fieldModels = new ArrayList<>();
            for (java.lang.reflect.Field field : fields) {
                if (isDataField(field)) {
                    fieldModels.add(new AnnotatedPojoField(this, field));
                }
            }
        }
        return fieldModels;
    }

    /**
     * Decide if a reflected field is a data field we need to process. A field
     * is considered a POJO field if it is not native, not transient, not static
     * and not annotated with the @Ignore annotation.
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

}
