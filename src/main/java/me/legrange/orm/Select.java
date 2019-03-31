package me.legrange.orm;

/**
 *
 * @author gideon
 * @param <T>
 * @param <O>
 */
public interface Select<T extends Table<O>, O, RT extends Table<RO>, RO> extends Executable<RT, RO>, Order<RT, RO> {

    <RT extends Table<RO>, RO> Join<T, O, RT, RO> join(RT table);

    <F extends NumberField<RT, RO, C>, C extends Number> NumberClause<T, O, C, RT, RO> where(F field);

    <F extends StringField<RT, RO>> StringClause<T, O, RT, RO> where(F field);

    <F extends DateField<RT, RO>> DateClause<T, O, RT, RO> where(F field);

    <F extends EnumField<RT, RO, C>, C extends Enum> EnumClause<T, O, C, RT, RO> where(F field);

    <F extends BooleanField<RT, RO>> BooleanClause<T, O, RT, RO> where(F field);

}
