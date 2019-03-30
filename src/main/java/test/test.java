package test;

import adept.aims.classes.Client;
import java.sql.Connection;
import java.util.List;
import me.legrange.orm.Orm;
import me.legrange.orm.OrmException;
import static test.Tables.CLIENT;

/**
 *
 * @author gideon
 */
public class test {

    public static void main(String... args) throws OrmException {
        Connection con = null;
        try (Orm orm = Orm.open(con)) {
            List<Client> list = orm.select(CLIENT)
                    .where(CLIENT.clientNumber).le(5).list();
//            List<Sale> list = orm.select(SALE)
//                    .where(SALE.status).notEq(Sale.SaleStatus.INACTIVE)
//                    .and(SALE.price).gt(50.0)
//                    .join(CLIENT).on(SALE.clientNumber).eq(CLIENT.clientNumber)
//                    .orderBy(SALE.saleNumber)
//                    .list();

        }
    }

}
