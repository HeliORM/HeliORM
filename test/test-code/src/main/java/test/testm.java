package test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 *
 * @author gideon
 */
public class testm {

    public static void main(String... args) throws Exception {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?user=root");
        testm t = new testm();
        t.test(con);
    }

    private void test(Connection con) throws SQLException {
        DatabaseMetaData metaData = con.getMetaData();
//        ResultSet tables = metaData.getTables("fivenines", null, "User", new String[]{"TABLE"});
        ResultSet columns = metaData.getColumns("fivenines", null, "User", null);
        ResultSetMetaData rsm = columns.getMetaData();
        while (columns.next()) {
            int idx = columns.getInt("ORDINAL_POSITION");
            String columnName = rsm.getColumnName(idx);
            int columnType = rsm.getColumnType(idx);
            String columnTypeName = rsm.getColumnTypeName(idx);
            System.out.printf("%s %s\n", columnName, columnTypeName);
        }
    }

}
