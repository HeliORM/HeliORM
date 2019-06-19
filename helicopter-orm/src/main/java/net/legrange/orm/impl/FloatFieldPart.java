package net.legrange.orm.impl;

import net.legrange.orm.def.FloatField;
import net.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class FloatFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Float> implements FloatField<T, O> {

    public FloatFieldPart(String javaName, String sqlName) {
        super(Float.class, javaName, sqlName);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.FLOAT;
    }

}
