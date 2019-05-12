package me.legrange.orm.impl;

import me.legrange.orm.IntegerField;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class IntegerFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Integer> implements IntegerField<T, O> {

    public IntegerFieldPart(String javaName, String sqlName) {
        super(Integer.class, javaName, sqlName);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.INTEGER;
    }

}
