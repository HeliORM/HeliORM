package net.legrange.orm.def;

import net.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public interface Join<LT extends Table<LO>, LO, RT extends Table<RO>, RO> {

    <L extends Field<LT, LO, C>, R extends Field<RT, RO, C>, C> OnClause<LT, LO, RT, RO> on(L leftField, R rightField);

}
