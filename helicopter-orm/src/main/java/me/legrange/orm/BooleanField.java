package me.legrange.orm;

/**
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface BooleanField<T extends Table<O>, O> extends Field<T, O, Boolean>, BooleanExpression<T, O> {

    @Override
    public default FieldType getFieldType() {
        return FieldType.BOOLEAN;
    }
}
