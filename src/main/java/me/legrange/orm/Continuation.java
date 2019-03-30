package me.legrange.orm;

/**
 ** @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface Continuation<T extends Table<O>, O> extends Executable<T, O> {

    <F extends NumberField<T, O, C>, C extends Number> NumberClause<T, O, C> and(F field);

    <F extends NumberField<T, O, C>, C extends Number> NumberClause<T, O, C> or(F field);

    <F extends StringField<T, O>> StringClause<T, O> and(F field);

    <F extends StringField<T, O>> StringClause<T, O> or(F field);

    <F extends EnumField<T, O, C>, C extends Enum> EnumClause<T, O, C> and(F field);

    <F extends EnumField<T, O, C>, C extends Enum> EnumClause<T, O, C> or(F field);

    <F extends DateField<T, O>> DateClause<T, O> and(F field);

    <F extends DateField<T, O>> DateClause<T, O> or(F field);
}
