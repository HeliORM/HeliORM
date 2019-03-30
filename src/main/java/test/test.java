package test;

import adept.aims.classes.Sale;
import java.sql.Connection;
import java.util.List;
import me.legrange.orm.Orm;
import me.legrange.orm.OrmException;
import static test.Tables.CLIENT;
import static test.Tables.SALE;

/**
 *
 * @author gideon
 */
public class test {

    public static void main(String... args) throws OrmException {
        Connection con = null;
        try (Orm orm = Orm.open(con, Orm.Dialect.MYSQL)) {
            orm.select(CLIENT)
                    .where(CLIENT.clientNumber).le(5)
                    .and(CLIENT.lastname).in("Le Grange").list();

            List<Sale> list = orm.select(SALE)
                    .where(SALE.status).notEq(Sale.SaleStatus.INACTIVE)
                    .orderBy(SALE.price)
                    .list();
        }
    }

}
