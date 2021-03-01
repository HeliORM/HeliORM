package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.ListField;

/**
 * @param <T> Type of table
 * @param <O> Type of POJO
 * @param <P> Type of the POJO
 * @author gideon
 */
public class ListFieldPart<T extends Table<O>, O, P> extends FieldPart<T, O, P> implements ListField<T, O, P> {

    public ListFieldPart(T table, String javaName, Class<P> javaType) {
        super(table, FieldType.LIST, javaType, javaName);
    }

}
