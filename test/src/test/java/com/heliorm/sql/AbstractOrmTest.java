package com.heliorm.sql;

import com.heliorm.Field;
import com.heliorm.Orm;
import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.sql.mysql.MySqlDriver;
import com.heliorm.sql.postgres.PostgreSqlDriver;
import com.heliorm.test.persons.Person;
import com.heliorm.test.pets.Cat;
import com.heliorm.test.pets.Dog;
import com.heliorm.test.place.Province;
import com.heliorm.test.place.Town;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

abstract class AbstractOrmTest {

    private static Orm orm;
    private static DataSource jdbcDataSource;
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
        String dbType = System.getenv("ORM_TEST_DB");
        dbType = (dbType == null) ? "" : dbType;
        Class<? extends SqlDriver> driver = switch (dbType) {
            case "mysql" -> {
                jdbcDataSource = setupMysqlDataSource();
                yield MySqlDriver.class;
            }
            case "postgresql" -> {
                jdbcDataSource = setupPostgreSqlDatasource();
                yield PostgreSqlDriver.class;
            }
            default -> {
                jdbcDataSource = setupH2DataSource();
                yield MySqlDriver.class;
            }
        };
        say("Using %s data source with driver %s", dbType, driver.getSimpleName());
        pops = new UnsafePojoOperations();
        orm = SqlOrmBuilder.create(AbstractOrmTest::getConnection, driver)
                .setCreateMissingTables(true)
                .setRollbackOnUncommittedClose(false)
                .setUseUnionAll(true)
                .build();
        deleteAll(Cat.class);
        deleteAll(Dog.class);
        deleteAll(Person.class);
        deleteAll(Town.class);
        deleteAll(Province.class);
    }

    protected static <O> List<O> createAll(List<O> data) throws OrmException {
        List<O> res = new ArrayList<>();
        for (O object : data) {
            res.add(orm.create(object));
        }
        return res;
    }

    protected static <O> List<O> selectAll(Class<O> type) throws OrmException {
        return orm.select(orm.tableFor(type)).list();
    }

    protected static <O> void deleteAll(Class<O> type) throws OrmException {
        for (O obj : selectAll(type)) {
            orm.delete(obj);
        }
    }

    protected static void say(String fmt, Object... args) {
        System.out.printf(fmt, args);
        System.out.println();
    }

    private static DataSource setupH2DataSource() {
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setUrl("jdbc:h2:mem:petz;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS petz;MODE=MYSQL;DATABASE_TO_UPPER=false");
        return jdbcDataSource;
    }

    private static DataSource setupMysqlDataSource() {
        HikariConfig conf = new HikariConfig();
        conf.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/petz");
        conf.setUsername("root");
        conf.setPassword("dev");
        return new HikariDataSource(conf);
    }

    private static DataSource setupPostgreSqlDatasource() {
        HikariConfig conf = new HikariConfig();
        conf.setJdbcUrl("jdbc:postgresql://127.0.0.1:5432/petz");
        conf.setUsername("postgres");
        conf.setPassword("dev");
        return new HikariDataSource(conf);
    }

    protected Orm orm() {
        return orm;
    }

    /**
     * Compare two POJOs to see that they are the same type and all their fields are the same.
     *
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

    /**
     * Compare two POJOs to see that they are the same type and all their fields are the same, apart from
     * their primary keys.
     *
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

    protected final <O> boolean listCompareAsIs(List<O> l1, List<O> l2) throws OrmException {
        if (l1.size() != l2.size()) {
            return false;
        }
        if (l1.isEmpty()) {
            return true;
        }
        for (int i = 0; i < l1.size(); ++i) {
            if (!pojoCompare(l1.get(i), l2.get(i))) {
                return false;
            }
        }
        return true;
    }

    protected final <O> boolean listCompareOrdered(List<O> l1, List<O> l2) throws OrmException {
        return listCompareAsIs(sort(l1), sort(l2));
    }

    private <O> List<O> sort(List<O> data) throws OrmException {
        if (data.size() < 2) {
            return data;
        }
        Table<O> t1 = orm.tableFor(data.getFirst());
        return data.stream().sorted((o1, o2) -> {
                    try {
                        int o = o1.getClass().getName().compareTo(o2.getClass().getName());
                        if (o == 0) {
                            return pops.compareTo(o1, o2, t1.getPrimaryKey().get());
                        }
                        return o;
                    } catch (OrmException e) {
                        return 0;
                    }
                })
                .collect(Collectors.toList());
    }
}
