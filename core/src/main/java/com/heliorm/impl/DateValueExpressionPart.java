package com.heliorm.impl;

import com.heliorm.Field;

import java.util.Date;

public final class DateValueExpressionPart< O> extends ValueExpressionPart<O, Date> {

    private final Date value;

    public DateValueExpressionPart(FieldPart left, Operator op, Date value) {
        super(Field.FieldType.DATE, left, op);
        this.value = value;
    }

    @Override
    public Date getValue() {
        return value;
    }


}

