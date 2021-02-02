package com.heliorm.impl;

import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public abstract class NumberFieldPart<T extends Table<O>, O, N extends Number> extends FieldPart<T, O, N> implements
        WithEqualsPart<T, O, N>,
        WithRangePart<T, O, N>,
        WithInPart<T, O, N>, WithIsPart<T,O,N>, com.heliorm.def.Field<T, O, N>, com.heliorm.def.Expression<T, O, N>, com.heliorm.def.WithRange<T, O, N>, com.heliorm.def.WithEquals<T, O, N>, com.heliorm.def.WithIn<T, O, N>, com.heliorm.def.WithIs<T, O, N> {

    public NumberFieldPart(T table, FieldType fieldType, Class<N> javaType, String javaName) {
        super(table, fieldType, javaType, javaName);
    }



}
