package net.legrange.orm;

import net.legrange.orm.def.Field;
import net.legrange.orm.driver.mysql.MySqlDriver;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;

import java.sql.Connection;
import java.sql.SQLException;

abstract class AbstractOrmTest {

    protected static Orm orm;
    private static JdbcDataSource jdbcDataSource;
    private static PojoOperations pops;

    private static Connection getConnection() {
        try {
            return jdbcDataSource.getConnection();
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @BeforeAll
    public static void setup() throws Exception {
        jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setUrl("jdbc:h2:mem:petz;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS petz;MODE=MYSQL;DATABASE_TO_UPPER=false");
        pops = new UnsafePojoOperations();
        orm = OrmBuilder.create(AbstractOrmTest::getConnection, MySqlDriver.class)
                .setCreateMissingTables(true)
                .setRollbackOnUncommittedClose(false)
                .build();
    }

    /** Compare two POJOs to see that they are the same type and all their fields are the same.
     *
     * @param o1
     * @param o2
     * @return
     * @throws OrmException
     */
    protected boolean pojoCompare(Object o1, Object o2) throws OrmException {
        Table<Object> t1 = orm.tableFor(o1);
        Table<Object> t2 = orm.tableFor(o2);
        if (t1 == t2) {
            for (Field field : t1.getFields()) {
                return pops.compareTo(o1, o2, field) == 0;
            }
        }
        return false;
    }

    /** Compare two POJOs to see that they are the same type and all their fields are the same, apart from
     * their primary keys.
     *
     * @param o1
     * @param o2
     * @return
     * @throws OrmException
     */
    protected boolean pojoCompareExcludingKey(Object o1, Object o2) throws OrmException {
        Table<Object> t1 = orm.tableFor(o1);
        Table<Object> t2 = orm.tableFor(o2);
        if (t1 != t2) {
            return false;
        }
        for (Field field : t1.getFields()) {
            if (!field.isPrimaryKey()) {
                if (pops.compareTo(o1, o2, field) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

}
