package net.legrange.orm;

import net.legrange.orm.driver.mysql.MySqlDriver;
import net.legrange.orm.pets.Cat;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static net.legrange.orm.TestData.makeCat;
import static org.junit.Assert.assertEquals;

public class CrudTests {

    private Orm orm;
    private JdbcDataSource jdbcDataSource;
    private Connection keepAlive;

    private Connection getConnection() {
        try {
            return jdbcDataSource.getConnection();
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
    }

    @Before
    public void setup() throws Exception {
        jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setUrl("jdbc:h2:mem:petz;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS petz;MODE=MYSQL;DATABASE_TO_UPPER=false;TRACE_LEVEL_SYSTEM_OUT=2");
        keepAlive = getConnection();
        orm = OrmBuilder.create(this::getConnection, MySqlDriver.class)
                .setCreateMissingTables(true)
                .setRollbackOnUncommittedClose(false)
                .build();
    }

    @Test
    public void testCreate() throws Exception {
        Cat cat = makeCat();
        Cat saved = orm.create(cat);
        assertEquals(cat.getName(), saved.getName());
    }

    @After
    public void cleanup() throws SQLException {
        keepAlive.close();
    }

}
