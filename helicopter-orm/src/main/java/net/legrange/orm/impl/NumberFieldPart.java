package net.legrange.orm.impl;

import net.legrange.orm.def.NumberField;
import net.legrange.orm.Table;

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

    public NumberFieldPart(Class<N> fieldClass, String javaName, String sqlName, boolean primaryKey) {
        super(fieldClass, javaName, sqlName, primaryKey);
    }

}
