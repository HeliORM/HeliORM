package test;

import adept.aims.classes.Client;
import adept.aims.classes.Sale;
import me.legrange.orm.DateField;
import me.legrange.orm.EnumField;
import me.legrange.orm.NumberField;
import me.legrange.orm.StringField;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class Tables {

    public static class ClientTable implements Table<Client> {

        public final NumberField<ClientTable, Client, Integer> clientNumber = null;
        public final StringField<ClientTable, Client> lastname = null;

    }

    public static class SaleTable implements Table<Sale> {

        public final NumberField<SaleTable, Sale, Integer> saleNumber = null;
        public final NumberField<SaleTable, Sale, Integer> clientNumber = null;
        public final NumberField<SaleTable, Sale, Double> price = null;
        public final EnumField<SaleTable, Sale, Sale.SaleStatus> status = null;
        public final DateField<SaleTable, Sale> created = null;

    }

    public static final ClientTable CLIENT = new ClientTable();
    public static final SaleTable SALE = new SaleTable();

}
