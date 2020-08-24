package com.heliorm;

import com.heliorm.driver.mysql.MySqlDriver;
import com.heliorm.def.Field;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    protected final boolean pojoCompare(Object o1, Object o2) throws OrmException {
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
    protected final boolean pojoCompareExcludingKey(Object o1, Object o2) throws OrmException {
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

    protected final <O> boolean listCompare(List<O> l1, List<O> l2) throws OrmException {
        if (l1.size() != l2.size()) {
            return false;
        }
        if (l1.isEmpty()) {
            return true;
        }
        Table<?> t1 = orm.tableFor(l1.get(0));
        l1 = sort(l1);
        l2 = sort(l2);
        for (int i = 0; i < l1.size(); ++i) {
            if (!pojoCompare(l1.get(i), l2.get(i))) {
                return false;
            }
        }
        return true;
    }

    protected final <O> List<O> createAll(List<O> data) throws OrmException {
        List<O> res = new ArrayList<>();
        for (O object : data) {
            res.add(orm.create(object));
        }
        return res;
    }

    private <O> List<O> sort(List<O> data) throws OrmException {
        Table<?> t1 = orm.tableFor(data.get(0));
        Collections.sort(data, (o1,o2) -> {
            try {
                return pops.compareTo(o1,o2,t1.getPrimaryKey().get());
            } catch (OrmException e) {
                return 0;
            }
        });
        return data;
    }

}
