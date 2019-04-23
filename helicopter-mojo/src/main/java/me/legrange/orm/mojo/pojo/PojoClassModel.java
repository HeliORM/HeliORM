package me.legrange.orm.mojo.pojo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import me.legrange.orm.annotation.Pojo;
import me.legrange.orm.mojo.FieldModel;
import me.legrange.orm.mojo.ClassModel;

/**
 *
 * @author gideon
 */
public class PojoClassModel implements ClassModel {

    private final Class<?> pojoClass;
    private List<FieldModel> fieldModels;

    public PojoClassModel(Class<?> pojoClass) {
        this.pojoClass = pojoClass;
    }

    @Override
    public String getClassName() {
        return pojoClass.getCanonicalName();
    }

    @Override
    public String getJavaName() {
        return pojoClass.getSimpleName();
    }

    @Override
    public String getTableName() {
        Optional<Pojo> pojo = getAnnotation(Pojo.class);
        if (pojo.isPresent()) {
            if (!pojo.get().tableName().isBlank()) {
                return pojo.get().tableName();
            }
        }
        return getJavaName();
    }

    @Override
    public List<FieldModel> getFields() {
        if (fieldModels == null) {
            List<Field> fields = getAllFields(pojoClass);
            fieldModels = new ArrayList();
            for (Field field : fields) {
                if (isDataField(field)) {
                    fieldModels.add(new PojoFieldModel(field));
                }
            }
        }
        return fieldModels;
    }

    private boolean isDataField(Field field) {
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
        return true;
    }

    private <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationClass) {
        return Optional.ofNullable(pojoClass.getAnnotation(annotationClass));
    }

    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> res = new LinkedList();
        if (clazz.getSuperclass() != Object.class) {
            res.addAll(getAllFields(clazz.getSuperclass()));
        }
        res.addAll(Arrays.asList(clazz.getDeclaredFields()));
        return res;
    }

}
