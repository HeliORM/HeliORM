package me.legrange.orm.impl;

import me.legrange.orm.NumberField;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public abstract class NumberFieldPart<T extends Table<O>, O, N extends Number> extends FieldPart<T, O, N> implements
        NumberField<T, O, N>,
        WithEqualsPart<T, O, N>,
        WithRangePart<T, O, N>,
        WithInPart<T, O, N> {

    public NumberFieldPart(Class<N> fieldType, String javaName, String sqlName) {
        super(fieldType, javaName, sqlName);
    }

}
