package me.legrange.orm;

/**
 *
 * @author gideon
 */
public interface Join<LT extends Table<LO>, LO, RT extends Table<RO>, RO> {

    <F extends Field<LT, LO, C>, C> OnClause<LT, LO, RT, RO, C> on(F field);

}
