package net.legrange.orm;

/**
 *
 * @author gideon
 */
public interface NumberField<T extends Table<O>, O, C extends Number> extends Field<T, O, C>, NumberExpression<T, O, C> {

}
