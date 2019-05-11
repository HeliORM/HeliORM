package me.legrange.orm.impl;

import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class ShortFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Short> {

    public ShortFieldPart(String javaName, String sqlName) {
        super(Short.class, javaName, sqlName);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.SHORT;
    }

}
