package net.legrange.orm.driver;

import java.sql.Connection;
import net.legrange.orm.OrmException;

/**
 *
 * @author gideon
 */
public final class MySqlOrm extends SqlOrm {

    public MySqlOrm(Connection con) throws OrmException {
        super(con);
    }

}
