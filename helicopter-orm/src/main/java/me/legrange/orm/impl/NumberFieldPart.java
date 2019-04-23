package me.legrange.orm.impl;

import me.legrange.orm.NumberField;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class NumberFieldPart<T extends Table<O>, O, N extends Number> extends FieldPart<T, O, N> implements NumberField<T, O, N> {

    public NumberFieldPart(Class<N> typeClass, String javaName, String sqlName) {
        super(typeClass, javaName, sqlName);
    }

}
