package me.legrange.orm;

/**
 *
 * @author gideon
 * @param <T>
 * @param <O>
 */
public interface Select<T extends Table<O>, O, RT extends Table<RO>, RO> extends Executable<RT, RO> {

    <F extends NumberField<T, O, C>, C extends Number> NumberClause<T, O, C, RT, RO> where(F field);

    <F extends StringField<T, O>> StringClause<T, O, RT, RO> where(F field);

    <F extends DateField<T, O>> DateClause<T, O, RT, RO> where(F field);

    <F extends EnumField<T, O, C>, C extends Enum> EnumClause<T, O, C, RT, RO> where(F field);

    <RT extends Table<RO>, RO> Join<T, O, RT, RO> join(RT table);

}
