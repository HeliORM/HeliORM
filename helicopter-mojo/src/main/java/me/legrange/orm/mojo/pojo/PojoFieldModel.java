package me.legrange.orm.mojo.pojo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;
import me.legrange.orm.annotation.Column;
import me.legrange.orm.mojo.FieldModel;

/**
 *
 * @author gideon
 */
public class PojoFieldModel implements FieldModel {

    private final Field pojoField;

    public PojoFieldModel(Field pojoField) {
        this.pojoField = pojoField;
    }

    @Override
    public String getSqlName() {
        Optional<Column> col = getAnnotation(Column.class);
        if (col.isPresent()) {
            String name = col.get().fieldName();
            if ((name != null) && !name.isEmpty()) {
                return name;
            }
        }
        return pojoField.getName();
    }

    @Override
    public String getJavaName() {
        return pojoField.getName();
    }

    @Override
    public Type getType() {
        return Type.typeFor(pojoField.getType());
    }

    private <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationClass) {
        return Optional.ofNullable(pojoField.getAnnotation(annotationClass));
    }
}
