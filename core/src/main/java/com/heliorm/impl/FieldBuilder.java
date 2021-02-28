package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.UncaughtOrmException;
import com.heliorm.def.Field;

import java.util.Optional;

import static java.lang.String.format;

public class FieldBuilder<P extends FieldPart> {

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
    private boolean collection  = false;
    private Optional<Table<?>> collectionTable = Optional.empty();

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

    public FieldBuilder<P> withCollection(Table<?> table) {
        collectionTable = Optional.of(table);
        collection = true;
        return this;
    }

    public P build() {
        P  part = null;
        switch (fieldType) {
            case STRING:
                part = (P) new StringFieldPart(table, javaName);
                break;
            case BOOLEAN:
                part = (P) new BooleanFieldPart(table, javaName);
                break;
            case BYTE:
                part = (P) new ByteFieldPart(table, javaName);
                break;
            case SHORT:
                part = (P) new ShortFieldPart(table, javaName);
                break;
            case INTEGER:
                part = (P) new IntegerFieldPart(table, javaName);
                break;
            case LONG:
                part = (P) new LongFieldPart(table, javaName);
                break;
            case FLOAT:
                part = (P) new FloatFieldPart(table, javaName);
                break;
            case DOUBLE:
                part = (P) new DoubleFieldPart(table, javaName);
                break;
            case ENUM:
                part = (P) new EnumFieldPart(table, javaType, javaName);
                break;
            case DATE:
                part = (P) new DateFieldPart(table, javaName);
                break;
            case INSTANT:
                part = (P) new InstantFieldPart(table, javaName);
                break;
            case DURATION:
                part = (P) new DurationFieldPart(table, javaName);
                break;
            case SET :
                part = (P) new SetFieldPart(table, javaName, javaType);
                break;
            case LIST :
                part = (P) new ListFieldPart<>(table, javaName, javaType);
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
        part.setCollection(collection);
        part.setCollectionTable(collectionTable);
        return part;
    }


}
