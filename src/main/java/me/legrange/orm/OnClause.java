package me.legrange.orm;

/**
 *
 * @author gideon
 */
public interface OnClause<LT extends Table<LO>, LO, RT extends Table<RO>, RO, C> {

    <F extends Field<RT, RO, C>, C> Select<RT, RO, LT, LO> eq(F field);

}
