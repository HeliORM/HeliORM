package me.legrange.orm.impl;

import me.legrange.orm.Orm;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
abstract class Part<T extends Table<O>, O, RT extends Table<RO>, RO> {

    protected final Part left;

    protected Part(Part left) {
        this.left = left;
    }

    protected Orm getOrm() {
        return left.getOrm();
    }

    protected abstract String query();

    protected Table getReturnTable() {
        return left.getReturnTable();
    }

}
