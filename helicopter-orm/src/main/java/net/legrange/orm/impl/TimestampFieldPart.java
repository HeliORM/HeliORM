package net.legrange.orm.impl;

import java.time.Instant;
import net.legrange.orm.Table;
import net.legrange.orm.def.TimestampField;

/**
 *
 * @author gideon
 */
public class TimestampFieldPart<T extends Table<O>, O> extends FieldPart<T, O, Instant> implements
        TimestampField<T, O>,
        WithRangePart<T, O, Instant>,
        WithEqualsPart<T, O, Instant>,
        WithInPart<T, O, Instant> {

    public TimestampFieldPart(String javaName, String sqlName) {
        super(Instant.class, javaName, sqlName);
    }

}
