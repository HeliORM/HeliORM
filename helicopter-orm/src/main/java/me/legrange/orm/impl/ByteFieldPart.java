package me.legrange.orm.impl;

import me.legrange.orm.ByteField;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class ByteFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Byte> implements ByteField<T, O> {

    public ByteFieldPart(String javaName, String sqlName) {
        super(Byte.class, javaName, sqlName);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.BYTE;
    }

}
