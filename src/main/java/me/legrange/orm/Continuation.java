package me.legrange.orm;

/**
 ** @param <LT> Table type
 * @param <LO> Object type
 *
 * @author gideon
 * @param <RT>
 * @param <RO>
 */
public interface Continuation<LT extends Table<LO>, LO, RT extends Table<RO>, RO> extends Executable<LT, LO>, Order<LT, LO> {

    <F extends NumberField<LT, LO, C>, C extends Number> NumberClause<LT, LO, C, RT, RO> and(F field);

    <F extends NumberField<LT, LO, C>, C extends Number> NumberClause<LT, LO, C, RT, RO> or(F field);

    <F extends StringField<LT, LO>> StringClause<LT, LO, RT, RO> and(F field);

    <F extends StringField<LT, LO>> StringClause<LT, LO, RT, RO> or(F field);

    <F extends EnumField<LT, LO, C>, C extends Enum> EnumClause<LT, LO, C, RT, RO> and(F field);

    <F extends EnumField<LT, LO, C>, C extends Enum> EnumClause<LT, LO, C, RT, RO> or(F field);

    <F extends DateField<LT, LO>> DateClause<LT, LO, RT, RO> and(F field);

    <F extends DateField<LT, LO>> DateClause<LT, LO, RT, RO> or(F field);

    <RT extends Table<RO>, RO> Join<LT, LO, RT, RO> join(RT table);

}
