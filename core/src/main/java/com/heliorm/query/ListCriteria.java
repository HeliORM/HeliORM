package com.heliorm.query;

import java.util.List;

import com.heliorm.def.Field;

/**
 *
 * @author gideon
 */
public class ListCriteria extends FieldCriteria {

    public enum Operator {
        IN, NOT_IN;
    }

    private final List values;
    private final Operator operator;

    public ListCriteria(Field field, Operator operator, List values) {
        super(Type.LIST_FIELD, field);
        this.values = values;
        this.operator = operator;
    }

    public List getValues() {
        return values;
    }

    public Operator getOperator() {
        return operator;
    }

}
