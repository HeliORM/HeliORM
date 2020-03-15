package net.legrange.orm.def;

import java.time.Instant;
import net.legrange.orm.Table;

/**
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface TimestampField<T extends Table<O>, O> extends Field<T, O, Instant>, TimeExpresssion<T, O> {

    @Override
    public default FieldType getFieldType() {
        return FieldType.TIMESTAMP;
    }
}
