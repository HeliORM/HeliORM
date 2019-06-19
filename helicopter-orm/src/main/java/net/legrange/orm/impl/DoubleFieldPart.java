package net.legrange.orm.impl;

import net.legrange.orm.DoubleField;
import net.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class DoubleFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Double> implements DoubleField<T, O> {

    public DoubleFieldPart(String javaName, String sqlName) {
        super(Double.class, javaName, sqlName);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.DOUBLE;
    }

}
