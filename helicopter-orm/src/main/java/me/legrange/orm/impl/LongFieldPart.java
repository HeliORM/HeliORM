package me.legrange.orm.impl;

import me.legrange.orm.LongField;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class LongFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Long> implements LongField<T, O> {

    public LongFieldPart(String javaName, String sqlName) {
        super(Long.class, javaName, sqlName);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.LONG;
    }

}
