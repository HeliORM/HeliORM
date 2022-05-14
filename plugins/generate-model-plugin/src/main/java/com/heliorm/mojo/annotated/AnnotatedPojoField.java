package com.heliorm.mojo.annotated;

import com.heliorm.Field;
import com.heliorm.Table;
import com.heliorm.annotation.ForeignKey;
import com.heliorm.def.FieldOrder;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static com.heliorm.mojo.annotated.AnnotationHelper.getAnnotation;
import static com.heliorm.mojo.annotated.AnnotationHelper.getFieldName;
import static java.lang.String.format;

/**
 * @author gideon
 */
public class AnnotatedPojoField implements Field {

    private final Table table;
    private final java.lang.reflect.Field pojoField;

    public AnnotatedPojoField(Table table, java.lang.reflect.Field pojoField) {
        this.table = table;
        this.pojoField = pojoField;
    }

    @Override
    public Table<?> getTable() {
        return table;
    }

    @Override
    public String getSqlName() {
        Optional<String> opt = getFieldName(pojoField);
        if (opt.isPresent()) {
            String fieldName = opt.get();
            if (!fieldName.trim().isEmpty()) {
                return fieldName;
            }
        }
        return pojoField.getName();
    }

    @Override
    public String getJavaName() {
        return pojoField.getName();
    }

    @Override
    public Class getJavaType() {
        return pojoField.getType();
    }

    @Override
    public FieldType getFieldType() {
        Class<?> type = pojoField.getType();
        if (type.isPrimitive()) {
            switch (type.getSimpleName()) {
                case "long":
                    return FieldType.LONG;
                case "int":
                    return FieldType.INTEGER;
                case "short":
                    return FieldType.SHORT;
                case "byte":
                    return FieldType.BYTE;
                case "double":
                    return FieldType.DOUBLE;
                case "float":
                    return FieldType.FLOAT;
                case "boolean":
                    return FieldType.BOOLEAN;
            }
        } else if (Number.class.isAssignableFrom(type)) {
            switch (type.getSimpleName()) {
                case "Long":
                    return FieldType.LONG;
                case "Integer":
                    return FieldType.INTEGER;
                case "Short":
                    return FieldType.SHORT;
                case "Byte":
                    return FieldType.BYTE;
                case "Double":
                    return FieldType.DOUBLE;
                case "Float":
                    return FieldType.FLOAT;
            }
        } else if (Boolean.class.isAssignableFrom(type)) {
            return FieldType.BOOLEAN;
        } else if (String.class.isAssignableFrom(type)) {
            return FieldType.STRING;
        } else if (Date.class.isAssignableFrom(type)) {
            return FieldType.DATE;
        } else if (Instant.class.isAssignableFrom(type)) {
            return FieldType.INSTANT;
        } else if (Enum.class.isAssignableFrom(type)) {
            return FieldType.ENUM;
        }
        throw new AnnotatedPojoException(format("Unsuppored field type %s for field '%s' on %s",
                type.getSimpleName(), pojoField.getName(), pojoField.getDeclaringClass().getCanonicalName()));
    }

    @Override
    public boolean isPrimaryKey() {
        return AnnotationHelper.isPrimaryKey(pojoField);
    }

    @Override
    public boolean isAutoNumber() {
        return AnnotationHelper.isAutoNumber(pojoField);
    }

    @Override
    public Optional<Integer> getLength() {
        Optional<Integer> opt = AnnotationHelper.getLength(pojoField);
        if (opt.isPresent()) {
            if (opt.get() > 0) {
                return Optional.of(opt.get());
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean isNullable() {
        return AnnotationHelper.isNullable(pojoField);
    }

    @Override
    public Field getField() {
        return this;
    }

    @Override
    public FieldOrder asc() {
        return () -> AnnotatedPojoField.this;
    }

    @Override
    public FieldOrder desc() {
        return new FieldOrder() {

            @Override
            public Direction getDirection() {
                return Direction.DESC;
            }

            @Override
            public Field getField() {
                return AnnotatedPojoField.this;
            }
        };
    }

    @Override
    public boolean isForeignKey() {
        return AnnotationHelper.isForeignKey(pojoField);
    }

    @Override
    public Optional<Table<?>> getForeignTable() {
        Optional<ForeignKey> fk = getAnnotation(pojoField, ForeignKey.class);
        if (fk.isPresent()) {
            Class<?> type = fk.get().pojo();
            if (type != null) {
                return table.getDatabase().getTables().stream()
                        .filter(table -> table.getObjectClass().equals(type))
                        .findFirst();
            }
        }
        return Optional.empty();
    }

}
