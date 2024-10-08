package com.heliorm.impl;

import com.heliorm.Field;
import com.heliorm.Field.FieldType;
import com.heliorm.Table;

import java.time.Instant;
import java.util.Date;

public final class TableBuilder<T extends Table<O>, O> {

    private final T table;

    private TableBuilder(T table) {
        this.table = table;
    }

    public static <T extends Table<O>, O> TableBuilder<T, O> create(T table) {
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

    public FieldBuilder<InstantFieldPart<O>> instantField(String javaName) {
        return new FieldBuilder<>(table, Field.FieldType.INSTANT, Instant.class, javaName);
    }

    public FieldBuilder<LocalDateTimeFieldPart<O>> localDateTimeField(String javaName) {
        return new FieldBuilder<>(table, FieldType.LOCAL_DATE_TIME, Instant.class, javaName);
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

    public FieldBuilder<ByteArrayFieldPart<O>> byteArrayField(String javaName) {
        return new FieldBuilder<>(table, FieldType.BYTE_ARRAY, byte[].class, javaName);
    }
}
