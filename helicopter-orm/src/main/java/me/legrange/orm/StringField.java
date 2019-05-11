package me.legrange.orm;

/**
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface StringField<T extends Table<O>, O> extends Field<T, O, String>, StringExpression<T, O> {

    @Override
    public default FieldType getFieldType() {
        return FieldType.STRING;
    }

}
