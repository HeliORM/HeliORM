package me.legrange.orm;

/**
 ** @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 * @param <RT>
 * @param <RO>
 */
public interface Continuation<T extends Table<O>, O, RT extends Table<RO>, RO> extends Executable<RT, RO> {

    <F extends NumberField<T, O, C>, C extends Number> NumberClause<T, O, C, RT, RO> and(F field);

    <F extends NumberField<T, O, C>, C extends Number> NumberClause<T, O, C, RT, RO> or(F field);

    <F extends StringField<T, O>> StringClause<T, O, RT, RO> and(F field);

    <F extends StringField<T, O>> StringClause<T, O, RT, RO> or(F field);

    <F extends EnumField<T, O, C>, C extends Enum> EnumClause<T, O, C, RT, RO> and(F field);

    <F extends EnumField<T, O, C>, C extends Enum> EnumClause<T, O, C, RT, RO> or(F field);

    <F extends DateField<T, O>> DateClause<T, O, RT, RO> and(F field);

    <F extends DateField<T, O>> DateClause<T, O, RT, RO> or(F field);

    <RT extends Table<RO>, RO> Join<T, O, RT, RO> join(RT table);

}
