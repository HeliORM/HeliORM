package net.legrange.orm.impl;

import net.legrange.orm.def.ByteField;
import net.legrange.orm.Table;

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
