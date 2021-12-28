package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.Field;

import java.util.Date;

public final class DateValueExpressionPart<T extends Table<O>, O> extends ValueExpressionPart<T,O, Date> {

    private Date value;

    public DateValueExpressionPart(FieldPart left, Operator op, Date value) {
        super(Field.FieldType.DATE, left, op);
        this.value = value;
    }

    @Override
    public Date getValue() {
        return value;
    }

   


}

