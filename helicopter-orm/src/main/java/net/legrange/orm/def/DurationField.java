package net.legrange.orm.def;

import java.time.Duration;
import net.legrange.orm.Table;

/**
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface DurationField<T extends Table<O>, O> extends Field<T, O, Duration>, DurationExpression<T, O> {

    @Override
    public default FieldType getFieldType() {
        return FieldType.DURATION;
    }
}
