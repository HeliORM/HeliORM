package me.legrange.orm.impl;

import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class LongFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Long> {

    public LongFieldPart(String javaName, String sqlName) {
        super(Long.class, javaName, sqlName);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.LONG;
    }

}
