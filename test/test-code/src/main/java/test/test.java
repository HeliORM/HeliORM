package test;

import net.legrange.orm.Orm;
import net.legrange.orm.OrmBuilder;
import net.legrange.orm.OrmException;
import net.legrange.orm.OrmTransaction;
import net.legrange.orm.Table;
import net.legrange.orm.driver.mysql.MySqlDriver;
import net.legrange.orm.driver.postgresql.PostgreSqlDriver;
import test.pets.Bird;
import test.pets.Bird.Kind;
import test.pets.Cat;
import test.pets.Cat.Type;
import test.pets.Dog;
import test.pets.Fish;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

import static java.lang.System.out;
import static test.Tables.BIRD;
import static test.Tables.CAT;
import static test.Tables.PERSON;
import static test.Tables.PET;
import static test.Tables.TEST;
import static test.pets.Cat.Type.INDOOR;

/**
 * @author gideon
 */
public class test {

    private static Orm postgresOrm() throws OrmException {
        ConnectionPool pool = new ConnectionPool("jdbc:postgresql://127.0.0.1:5432/petz", "postgres", "dev");
        return OrmBuilder.create(() -> {
            try {
                return pool.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }, PostgreSqlDriver.class)
                .setRollbackOnUncommittedClose(false)
                .setCreateMissingTables(true)
//                .withPojoOperations(new BeanPojoOperations())
                .mapDatabase(TEST, "petz").build();

    }

    private static Orm mysqlOrm() throws OrmException {
        ConnectionPool pool = new ConnectionPool("jdbc:mysql://127.0.0.1:3306/petz", "test", "test");
        return OrmBuilder.create(() -> {
            try {
                return pool.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }, MySqlDriver.class)
                .setRollbackOnUncommittedClose(false)
                .setCreateMissingTables(true)
//                .withPojoOperations(new BeanPojoOperations())
                .mapDatabase(TEST, "petz").build();

    }

    public static void main(String... args) throws Exception {
        Orm orm = mysqlOrm();

        say("All cats");
        List<Cat> allCats = orm.select(CAT).list();
        say("All indoor cats");
        List<Cat> indoorCats = orm.select(CAT)
                .where(CAT.type.eq(INDOOR))
                .list();


        test t = new test();
        t.makeCat(orm);
        t.makeDog(orm);
//        t.makeFish(orm);
        t.makeBird(orm);
    }

    private void makeCat(Orm orm) throws OrmException {
        Cat cat = new Cat();
        cat.setType(INDOOR);
        cat.setAge(1);
        cat.setName("Top Cat");
        cat.setPersonNumber(1L);
        cat = orm.create(cat);
        say("Created cat '%s' with id %d", cat.getName(), cat.getId());
    }

    private void makeDog(Orm orm) throws OrmException {
        Dog dog = new Dog();
        dog.setAge(4);
        dog.setName("Mad Dog");
        dog.setPersonNumber(1L);
        dog = orm.create(dog);
        say("Created dog '%s' with id %d", dog.getName(), dog.getId());
    }

    private void makeBird(Orm orm) throws OrmException {
        Bird bird = new Bird();
        bird.setKind(Kind.FREERANGE);
        bird.setSingTime(Duration.ofMinutes(15));
        bird.setPersonNumber(1L);
        bird.setName("King Cock");
        bird = orm.create(bird);
        say("Created bird '%s' with id %d", bird.getName(), bird.getId());
    }


    private static void say(String fmt, Object... args) {
        out.printf(fmt, args);
        out.println();
    }

}
