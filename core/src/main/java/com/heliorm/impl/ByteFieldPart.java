package com.heliorm.impl;

import com.heliorm.def.ByteField;
import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public class ByteFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Byte> implements ByteField<T, O> {

    public ByteFieldPart(String javaName) {
        super(FieldType.BYTE, Byte.class, javaName);
    }


}
