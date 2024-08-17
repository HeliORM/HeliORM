package com.heliorm.impl;

import com.heliorm.Field;
import com.heliorm.Table;

import java.util.Optional;

public final class FieldBuilder<P extends FieldPart<?,?>> {

    private final Table<?> table;
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

    public FieldBuilder(Table<?> table, Field.FieldType fieldType, Class<?> javaType, String javaName) {
        this.table = table;
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
        P part = switch (fieldType) {
            case STRING -> (P) new StringFieldPart(table, javaName);
            case BOOLEAN -> (P) new BooleanFieldPart(table, javaName);
            case BYTE -> (P) new ByteFieldPart(table, javaName);
            case SHORT -> (P) new ShortFieldPart(table, javaName);
            case INTEGER -> (P) new IntegerFieldPart(table, javaName);
            case LONG -> (P) new LongFieldPart(table, javaName);
            case FLOAT -> (P) new FloatFieldPart(table, javaName);
            case DOUBLE -> (P) new DoubleFieldPart(table, javaName);
            case ENUM -> (P) new EnumFieldPart(table, javaType, javaName);
            case DATE -> (P) new DateFieldPart(table, javaName);
            case INSTANT -> (P) new InstantFieldPart(table, javaName);
            case LOCAL_DATE_TIME -> (P) new LocalDateTimeFieldPart<>(table, javaName);
        };
        part.setAutoNumber(autoNumber);
        part.setForeignKey(foreignKey);
        part.setForeignTable(foreignTable);
        part.setNullable(nullable);
        part.setPrimaryKey(primaryKey);
        part.setSqlName(sqlName);
        part.setLength(length);
        return part;
    }

}
