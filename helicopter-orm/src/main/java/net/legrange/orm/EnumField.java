package net.legrange.orm;

/**
 *
 * @author gideon
 */
public interface EnumField<T extends Table<O>, O, C extends Enum> extends Field<T, O, C>, EnumExpression<T, O, C> {

    @Override
    public default FieldType getFieldType() {
        return FieldType.ENUM;
    }
}