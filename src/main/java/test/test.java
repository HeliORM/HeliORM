package test;

import adept.aims.classes.Client;
import adept.aims.classes.Sale;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

    public static void main(String... args) throws OrmException, SQLException {
        Connection con = DriverManager.getConnection("jdbc:mysql://aims.adept.za.net:3306/adept_aimsdev", "gideon", "fudge55");
        try (Orm orm = Orm.open(con, Orm.Driver.MYSQL)) {
            List<Client> list = orm.select(CLIENT)
                    .where(CLIENT.clientNumber).le(5)
                    .or(CLIENT.lastname).in("Smith", "Jones")
                    .list();
            List<Sale> list1 = orm.select(SALE)
                    .where(SALE.status).notEq(Sale.SaleStatus.INACTIVE)
                    .and(SALE.price).gt(50.0)
                    .join(CLIENT).on(SALE.clientNumber, CLIENT.clientNumber)
                    .where(CLIENT.type).eq(Client.ClientType.PRIVATE)
                    .orderBy(SALE.status).thenByDesc(SALE.price)
                    .list();

        }
    }

}
