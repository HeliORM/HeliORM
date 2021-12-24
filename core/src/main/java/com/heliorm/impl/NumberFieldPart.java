package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Field;
import com.heliorm.def.WithEquals;
import com.heliorm.def.WithIn;
import com.heliorm.def.WithIs;
import com.heliorm.def.WithRange;

/**
 * @author gideon
 */
public abstract class NumberFieldPart<T extends Table<O>, O, N extends Number> extends FieldPart<T, O, N> implements
        WithEqualsPart<T, O, N>, WithRangePart<T, O, N>,
        WithInPart<T, O, N>, WithIsPart<T, O, N>, Field<T, O, N>, WithRange<T, O, N>, WithEquals<T, O, N>, WithIn<T, O, N>, WithIs<T, O, N> {

    public NumberFieldPart(T table, FieldType fieldType, Class<N> javaType, String javaName) {
        super(table, fieldType, javaType, javaName);
    }


}
