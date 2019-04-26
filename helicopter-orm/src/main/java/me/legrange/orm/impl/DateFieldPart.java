package me.legrange.orm.impl;

import java.util.Date;
import me.legrange.orm.DateField;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class DateFieldPart<T extends Table<O>, O> extends FieldPart<T, O, Date> implements
        DateField<T, O>,
        WithRangePart<T, O, Date>,
        WithEqualsPart<T, O, Date>,
        WithInPart<T, O, Date> {

    public DateFieldPart(String javaName, String sqlName) {
        super(Date.class, javaName, sqlName);
    }

}
