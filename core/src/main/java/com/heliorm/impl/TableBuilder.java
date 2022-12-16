package com.heliorm.impl;

import com.heliorm.Field;
import com.heliorm.Table;

import java.util.Date;

public final class TableBuilder<T extends Table<O>, O> {

    private final T table;

    private TableBuilder(T table) {
        this.table = table;
    }

    public static <T extends Table<O>, O> TableBuilder<T, O> create(T table, Class<O> pojoType) {
        return new TableBuilder<>(table);
    }

    public FieldBuilder<ByteFieldPart<O>> byteField(String javaName) {
        return new FieldBuilder<>(table, Field.FieldType.BYTE, Byte.class, javaName);
    }

    public FieldBuilder<ShortFieldPart<O>> shortField(String javaName) {
        return new FieldBuilder<>(table, Field.FieldType.SHORT, Short.class, javaName);
    }

    public FieldBuilder<IntegerFieldPart<O>> integerField(String javaName) {
        return new FieldBuilder<>(table, Field.FieldType.INTEGER, Integer.class, javaName);
    }

    public FieldBuilder<LongFieldPart<O>> longField(String javaName) {
        return new FieldBuilder<>(table, Field.FieldType.LONG, Long.class, javaName);
    }

    public FieldBuilder<FloatFieldPart<O>> floatField(String javaName) {
        return new FieldBuilder<>(table, Field.FieldType.FLOAT, Float.class, javaName);
    }

    public FieldBuilder<DoubleFieldPart<O>> doubleField(String javaName) {
        return new FieldBuilder<>(table, Field.FieldType.DOUBLE, Double.class, javaName);
    }

    public FieldBuilder<DateFieldPart<O>> dateField(String javaName) {
        return new FieldBuilder<>(table, Field.FieldType.DATE, Date.class, javaName);
    }

    public FieldBuilder<StringFieldPart<O>> stringField(String javaName) {
        return new FieldBuilder<>(table, Field.FieldType.STRING, String.class, javaName);
    }

    public FieldBuilder<BooleanFieldPart<O>> booleanField(String javaName) {
        return new FieldBuilder<>(table, Field.FieldType.BOOLEAN, Boolean.class, javaName);
    }

    public <E extends Enum<E>> FieldBuilder<EnumFieldPart<O, E>> enumField(String javaName, Class<E> enumType) {
        return new FieldBuilder<>(table, Field.FieldType.ENUM, enumType, javaName);
    }

    public <P> FieldBuilder<SetFieldPart<T,O, P>> setField(String javaName,Class<P> pojoType) {
        return new FieldBuilder<>(table, Field.FieldType.SET,  pojoType, javaName);
    }

    public <P> FieldBuilder<ListFieldPart<O, P>> listField(String javaName,Class<P> pojoType) {
        return new FieldBuilder<>(table, Field.FieldType.LIST,  pojoType, javaName);
    }

}
