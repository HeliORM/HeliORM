package test;

import adept.aims.classes.Client;
import adept.aims.classes.Sale;
import java.util.Arrays;
import java.util.List;
import me.legrange.orm.DateField;
import me.legrange.orm.EnumField;
import me.legrange.orm.Field;
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

        @Override
        public Class<Client> getObjectClass() {
            return Client.class;
        }

        @Override
        public String getSqTable() {
            return "client";
        }

        public List<Field> getFields() {
            return Arrays.asList(clientNumber, lastname);
        }

    }

    public static class SaleTable implements Table<Sale> {

        public final NumberField<SaleTable, Sale, Integer> saleNumber = null;
        public final NumberField<SaleTable, Sale, Integer> clientNumber = null;
        public final NumberField<SaleTable, Sale, Double> price = null;
        public final EnumField<SaleTable, Sale, Sale.SaleStatus> status = null;
        public final DateField<SaleTable, Sale> created = null;

        @Override
        public String getSqTable() {
            return "sale";
        }

        public List<Field> getFields() {
            return Arrays.asList(saleNumber, clientNumber, price, status, created);
        }

        @Override
        public Class<Sale> getObjectClass() {
            return Sale.class;
        }
    }

    public static final ClientTable CLIENT = new ClientTable();
    public static final SaleTable SALE = new SaleTable();

}
