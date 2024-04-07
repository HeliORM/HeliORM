package com.heliorm.mojo.annotated;

import com.heliorm.Field;
import com.heliorm.Table;
import com.heliorm.annotation.Column;
import com.heliorm.annotation.ForeignKey;
import com.heliorm.annotation.PrimaryKey;
import com.heliorm.def.FieldOrder;

import java.lang.reflect.RecordComponent;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static java.lang.String.format;

public final class AnnotatedRecordField<O, C> implements Field<O,C> {

    private final Table<O> table;
    private final RecordComponent component;

    public AnnotatedRecordField(Table<O> table, RecordComponent component) {
        this.table = table;
        this.component = component;
    }

    @Override
    public Table<?> getTable() {
        return table;
    }

    @Override
    public FieldType getFieldType() {
        Class<?> type = component.getType();
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
        } else if (LocalDateTime.class.isAssignableFrom(type)) {
            return FieldType.LOCAL_DATE_TIME;
        } else if (Enum.class.isAssignableFrom(type)) {
            return FieldType.ENUM;
        }
        throw new AnnotatedPojoException(format("Unsupported field type %s for field '%s' on %s",
                type.getSimpleName(), component.getName(), component.getDeclaringRecord().getCanonicalName()));
    }

    @Override
    public Class<C> getJavaType() {
        return (Class<C>) component.getType();
    }

    @Override
    public String getJavaName() {
        return component.getName();
    }

    @Override
    public String getSqlName() {
        var col = component.getAnnotation(Column.class);
        if (col != null) {
            String name = col.fieldName();
            if ((name != null) && !name.isEmpty()) {
                return name;
            }
        }
        return component.getName();
    }

    @Override
    public boolean isPrimaryKey() {
        return component.getAnnotation(PrimaryKey.class) != null;
    }

    @Override
    public boolean isForeignKey() {
        return component.getAnnotation(ForeignKey.class) != null;
    }

    @Override
    public boolean isAutoNumber() {
        var pk =  component.getAnnotation(PrimaryKey.class);
        return pk != null && pk.autoIncrement();
    }

    @Override
    public Optional<Table<?>> getForeignTable() {
        var fk = component.getAnnotation(ForeignKey.class);
        if (fk != null) {
            Class<?> type = fk.pojo();
            if (type != null) {
                return table.getDatabase().getTables().stream()
                        .filter(table -> table.getObjectClass().equals(type))
                        .findFirst();
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getLength() {
        var col = component.getAnnotation(Column.class);
        if (col != null) {
            if (col.length() > 0) {
                return Optional.of(col.length());
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean isNullable() {
        var col = component.getAnnotation(Column.class);
        return col != null && col.nullable();
    }

    @Override
    public FieldOrder asc() {
        return () -> AnnotatedRecordField.this;
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
                return AnnotatedRecordField.this;
            }
        };
    }

    @Override
    public Field<O, C> getField() {
        return this;
    }
}
