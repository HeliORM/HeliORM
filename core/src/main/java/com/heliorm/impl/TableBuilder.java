package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.Field;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public final class TableBuilder<T extends Table<O>, O> {

    private final T table;

    public static <T extends Table<O>, O> TableBuilder<T,O> create(T table, Class<O> pojoType) {
            return new TableBuilder<T,O>(table);
    }

    public FieldBuilder< ByteFieldPart<T,O>> byteField(String javaName) {
        return new FieldBuilder<>(table, Field.FieldType.BYTE, Byte.class, javaName);
    }

    public FieldBuilder<ShortFieldPart<T,O>> shortField(String javaName) {
        return new FieldBuilder<>(table, Field.FieldType.SHORT, Short.class, javaName);
    }

    public FieldBuilder<IntegerFieldPart<T,O>> integerField(String javaName) {
        return new FieldBuilder<>(table, Field.FieldType.INTEGER, Integer.class, javaName);
    }

    public FieldBuilder<LongFieldPart<T,O>> longField(String javaName) {
            return new FieldBuilder<>(table, Field.FieldType.LONG, Long.class, javaName);
    }

    public FieldBuilder<FloatFieldPart<T,O>> floatField(String javaName) {
        return new FieldBuilder<>(table, Field.FieldType.FLOAT, Float.class, javaName);
    }

    public FieldBuilder<DoubleFieldPart<T,O>> doubleField(String javaName) {
        return new FieldBuilder<>(table, Field.FieldType.DOUBLE, Double.class, javaName);
    }

    public FieldBuilder<DateFieldPart<T,O>> dateField(String javaName) {
        return new FieldBuilder<>(table, Field.FieldType.DATE, Date.class, javaName);
    }

    public FieldBuilder<DurationFieldPart<T,O>> durationField(String javaName) {
        return new FieldBuilder<>(table, Field.FieldType.DURATION, Duration.class, javaName);
    }

    public FieldBuilder<InstantFieldPart<T,O>> timestampField(String javaName) {
        return new FieldBuilder<>(table, Field.FieldType.INSTANT, Instant.class, javaName);
    }

    public FieldBuilder<StringFieldPart<T,O>> stringField(String javaName) {
        return new FieldBuilder<>(table, Field.FieldType.STRING, String.class, javaName);
    }

    public FieldBuilder<BooleanFieldPart<T,O>> booleanField(String javaName) {
        return new FieldBuilder<>(table, Field.FieldType.BOOLEAN, Boolean.class, javaName);
    }

    public <E extends Enum> FieldBuilder<EnumFieldPart<T,O,E>> enumField(String javaName, Class<E> enumType ) {
        return new FieldBuilder<>(table, Field.FieldType.ENUM,  enumType, javaName);
    }

    private TableBuilder(T table) {
        this.table = table;
    }
}
