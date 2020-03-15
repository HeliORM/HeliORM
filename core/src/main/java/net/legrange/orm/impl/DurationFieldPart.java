package net.legrange.orm.impl;

import java.time.Duration;
import net.legrange.orm.Table;
import net.legrange.orm.def.DurationField;

/**
 *
 * @author gideon
 */
public class DurationFieldPart<T extends Table<O>, O> extends FieldPart<T, O, Duration> implements
        DurationField<T, O>,
        WithRangePart<T, O, Duration>,
        WithEqualsPart<T, O, Duration>,
        WithInPart<T, O, Duration> {

    public DurationFieldPart(String javaName, String sqlName) {
        super(Duration.class, javaName, sqlName);
    }

}
