package me.legrange.orm.impl;

import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class FloatFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Float> {

    public FloatFieldPart(String javaName, String sqlName) {
        super(Float.class, javaName, sqlName);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.FLOAT;
    }

}
