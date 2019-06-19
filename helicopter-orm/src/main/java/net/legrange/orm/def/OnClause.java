package net.legrange.orm.def;

import net.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public interface OnClause<LT extends Table<LO>, LO, RT extends Table<RO>, RO> extends Select<LT, LO, RT, RO> {

}
