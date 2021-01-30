package com.heliorm.impl;

import com.heliorm.def.DateField;
import com.heliorm.Table;

import java.util.Date;

/**
 *
 * @author gideon
 */
public class DateFieldPart<T extends Table<O>, O> extends FieldPart<T, O, Date> implements
        DateField<T, O>,
        WithRangePart<T, O, Date>,
        WithEqualsPart<T, O, Date>,
        WithInPart<T, O, Date>, WithIsPart<T, O, Date> {

    public DateFieldPart(T table, String javaName) {
        super(table, FieldType.DATE, Date.class, javaName);
    }

}
