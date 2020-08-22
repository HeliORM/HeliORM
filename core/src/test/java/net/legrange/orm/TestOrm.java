package net.legrange.orm;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.function.Supplier;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.legrange.orm.driver.mysql.MySqlDriver;

public class TestOrm  {

    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;
    private static TestOrm instance;
    private Orm orm;

    private TestOrm() throws OrmException {
        config.setJdbcUrl(DB_CONNECTION);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        ds = new HikariDataSource(config);
        orm = OrmBuilder.create(this::getConnection, MySqlDriver.class)
                .setCreateMissingTables(true)
                .setRollbackOnUncommittedClose(false)
                .build();
    }

    Connection getConnection()  {
        try {
            return ds.getConnection();
        }
        catch (SQLException ex) {
            throw  new RuntimeException(ex.getMessage(), ex);
        }
    }

    public static Orm orm() throws OrmException {
        if (instance == null) {
            instance= new TestOrm();
        }
        return instance.orm;
    }
}
