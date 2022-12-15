package com.heliorm.impl;

import com.heliorm.Field;
import com.heliorm.Table;
import com.heliorm.def.WithEquals;
import com.heliorm.def.WithIn;
import com.heliorm.def.WithIs;
import com.heliorm.def.WithRange;

/**
 * @author gideon
 */
public abstract class NumberFieldPart<O, N extends Number> extends FieldPart<O, N> implements
        WithEqualsPart<O, N>, WithRangePart<O, N>,
        WithInPart<O, N>, WithIsPart<O, N>, Field<O, N>, WithRange<O, N>, WithEquals<O, N>, WithIn<O, N>, WithIs<O> {

    public NumberFieldPart(Table<O> table, FieldType fieldType, Class<N> javaType, String javaName) {
        super(table, fieldType, javaType, javaName);
    }


}
