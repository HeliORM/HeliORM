package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.ListField;

/**
 * @param <T> Type of table
 * @param <O> Type of POJO
 * @param <P> Type of the POJO
 * @author gideon
 */
public class ListFieldPart<O, P> extends FieldPart<O, P> implements ListField<O, P> {

    public ListFieldPart(Table<O> table, String javaName, Class<P> javaType) {
        super(table, FieldType.LIST, javaType, javaName);
    }

}
