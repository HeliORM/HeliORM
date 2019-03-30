package me.legrange.orm;

/**
 *
 * @author gideon
 * @param <T>
 * @param <O>
 */
public interface Select<T extends Table<O>, O> extends Executable<T, O> {

    <F extends NumberField<T, O, C>, C extends Number> NumberClause<T, O, C> where(F field);

    <F extends StringField<T, O>> StringClause<T, O> where(F field);

    <F extends DateField<T, O>> DateClause<T, O> where(F field);

    <F extends EnumField<T, O, C>, C extends Enum> EnumClause<T, O, C> where(F field);

}
