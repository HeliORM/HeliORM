package net.legrange.orm.impl;

import net.legrange.orm.def.LongField;
import net.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class LongFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Long> implements LongField<T, O> {

    public LongFieldPart(String javaName, String sqlName) {
        super(Long.class, javaName, sqlName);
    }

    public LongFieldPart(String javaName, String sqlName, boolean primaryKey) {
        super(Long.class, javaName, sqlName, primaryKey);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.LONG;
    }

}
