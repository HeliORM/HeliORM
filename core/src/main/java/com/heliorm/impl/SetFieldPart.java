package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.SetField;

/**
 * @param <T> Type of table
 * @param <O> Type of POJO
 * @param <P> Type of the POJO
 * @author gideon
 */
public class SetFieldPart<T extends Table<O>, O, P> extends FieldPart<T, O, P> implements SetField<T, O, P> {

    public SetFieldPart(T table, String javaName, Class<P> javaType) {
        super(table, FieldType.SET, javaType, javaName);
    }

}
