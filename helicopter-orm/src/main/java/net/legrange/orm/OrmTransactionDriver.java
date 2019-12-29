package net.legrange.orm;

import net.legrange.orm.OrmException;
import net.legrange.orm.OrmTransaction;

public interface OrmTransactionDriver {

    OrmTransaction openTransaction() throws OrmException;

    void setRollbackOnUncommittedClose(boolean rollback);

}
