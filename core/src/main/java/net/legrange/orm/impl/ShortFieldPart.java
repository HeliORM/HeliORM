package net.legrange.orm.impl;

import net.legrange.orm.def.ShortField;
import net.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class ShortFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Short> implements ShortField<T, O> {

    public ShortFieldPart(String javaName, String sqlName) {
        super(Short.class, javaName, sqlName);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.SHORT;
    }

}