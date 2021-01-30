package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.UncaughtOrmException;
import com.heliorm.def.Field;

import java.util.Optional;

import static java.lang.String.format;

public class FieldBuilder<P extends FieldPart> {

    private final Field.FieldType fieldType;
    private final Class<?> javaType;
    private final String javaName;
    private String sqlName;
    private boolean primaryKey = false;
    private boolean autoNumber = false;
    private boolean foreignKey = false;
    private boolean nullable = false;
    private Optional<Table<?>> foreignTable = Optional.empty();
    private Optional<Integer> length = Optional.empty();

    public FieldBuilder(Field.FieldType fieldType, Class<?> javaType, String javaName) {
        this.fieldType = fieldType;
        this.javaType = javaType;
        this.javaName = javaName;
    }

    public FieldBuilder<P> withSqlName(String value) {
        this.sqlName = value;
        return this;
    }

    public FieldBuilder<P> withPrimaryKey(boolean value) {
        this.primaryKey = value;
        return this;
    }

    public FieldBuilder<P> withAutoNumber(boolean value) {
        this.autoNumber = value;
        return this;
    }
    public FieldBuilder<P> withNullable(boolean value) {
        this.nullable = value;
        return this;
    }
    public FieldBuilder<P> withForeignKey(boolean value) {
        this.foreignKey = value;
        return this;
    }

    public FieldBuilder<P> withForeignTable(Table<?> value) {
        this.foreignTable = Optional.ofNullable(value);
        return this;
    }

    public FieldBuilder<P> withLength(int value) {
        this.length = Optional.of(value);
        return this;
    }

    public P build() {
        P  part = null;
        switch (fieldType) {
            case STRING:
                part = (P) new StringFieldPart(javaName);
                break;
            case BOOLEAN:
                part = (P) new BooleanFieldPart(javaName);
                break;
            case BYTE:
                part = (P) new ByteFieldPart(javaName);
                break;
            case SHORT:
                part = (P) new ShortFieldPart(javaName);
                break;
            case INTEGER:
                part = (P) new IntegerFieldPart(javaName);
                break;
            case LONG:
                part = (P) new LongFieldPart(javaName);
                break;
            case FLOAT:
                part = (P) new FloatFieldPart(javaName);
                break;
            case DOUBLE:
                part = (P) new DoubleFieldPart(javaName);
                break;
            case ENUM:
                part = (P) new EnumFieldPart(javaType, javaName);
                break;
            case DATE:
                part = (P) new DateFieldPart(javaName);
                break;
            case TIMESTAMP:
                part = (P) new TimestampFieldPart(javaName);
                break;
            case DURATION:
                part = (P) new DurationFieldPart(javaName);
                break;
            default:
                throw new UncaughtOrmException(format("Unexpected field type %s. BUG!", fieldType));
        }
        part.setAutoNumber(autoNumber);
        part.setForeignKey(foreignKey);
        part.setForeignTable(foreignTable);
        part.setNullable(nullable);
        part.setPrimaryKey(primaryKey);
        part.setSqlName(sqlName);
        return part;
    }


}
