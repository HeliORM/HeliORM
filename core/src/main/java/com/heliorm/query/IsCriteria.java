package com.heliorm.query;

import com.heliorm.def.Field;

/**
 *
 * @author gideon
 */
public class IsCriteria extends FieldCriteria {

    public enum Operator {
        IS_NULL, IS_NOT_NULL;
    }

    private final Operator operator;

    public IsCriteria(Field field, Operator operator) {
        super(Type.IS_FIELD, field);
        this.operator = operator;
    }

    public Operator getOperator() {
        return operator;
    }

}
