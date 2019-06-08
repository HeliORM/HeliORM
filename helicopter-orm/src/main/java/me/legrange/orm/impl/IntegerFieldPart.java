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

    public IntegerFieldPart(String javaName, String sqlName, boolean primaryKey) {
        super(Integer.class, javaName, sqlName, primaryKey);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.INTEGER;
    }

}
