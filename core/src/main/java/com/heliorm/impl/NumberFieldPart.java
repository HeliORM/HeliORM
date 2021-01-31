package com.heliorm.impl;

import com.heliorm.def.NumberField;
import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public abstract class NumberFieldPart<T extends Table<O>, O, N extends Number> extends FieldPart<T, O, N> implements
        NumberField<T, O, N>,
        WithEqualsPart<T, O, N>,
        WithRangePart<T, O, N>,
        WithInPart<T, O, N>, WithIsPart<T,O,N> {

    public NumberFieldPart(T table, FieldType fieldType, Class<N> javaType, String javaName) {
        super(table, fieldType, javaType, javaName);
    }



}
