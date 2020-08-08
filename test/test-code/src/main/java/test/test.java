package test;

import net.legrange.orm.Orm;
import net.legrange.orm.OrmBuilder;
import net.legrange.orm.OrmException;
import net.legrange.orm.OrmTransaction;
import net.legrange.orm.Table;
import net.legrange.orm.driver.mysql.MySqlDriver;
import net.legrange.orm.driver.postgresql.PostgreSqlDriver;
import test.persons.Person;
import test.pets.*;
import test.pets.Bird.Kind;
import test.pets.Cat.Type;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.Random;

import static java.lang.String.format;
import static java.lang.System.out;
import static test.Tables.BIRD;
import static test.Tables.CAT;
import static test.Tables.PERSON;
import static test.Tables.PET;
import static test.Tables.TEST;

/**
 * @author gideon
 */
public class test {

    private static final String names[] = {
            "Andre", "Barry", "Carl", "Dannny", "Eddy", "Fred", "Garth", "Harry", "India",
            "Julie","Katy", "Lena", "Mary", "Natasha", "Orpha", "Peter", "Quintin", "Riaan",
            "Steve", "Tyronne", "Una", "Vicky", "Xena", "Ysbeer", "Zorro"
    };

    private Random rand = new Random();

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
//        Orm orm = mysqlOrm();
        Orm orm = postgresOrm();

        test t = new test();
        t.makeSomeData(orm);
        t.abstractSelect(orm);
    }

    private String getName() {
        return names[rand.nextInt(names.length)];
    }

    private void abstractSelect(Orm orm) throws OrmException {
        List<Pet> pets = orm.select(PET)
                .where(PET.name.notEq("Peter"))
                .orderBy(PET.name).thenBy(PET.age)
                .list();
        pets.forEach(System.out::println);
    }

    private void makeSomeData(Orm orm) throws OrmException {
        for(int i  =0; i < 3; ++i) {
            Person person = makePerson(orm);
            makeCat(orm, person);
            makeDog(orm,person);
            makeFish(orm, person);
            makeBird(orm, person);
        }
    }


    private Person makePerson(Orm orm) throws OrmException {
            Person p = new Person();
            String name = getName();
            p.setEmailAddress(format("%s@gmail.com", getName().toLowerCase()));
            p.setFirstName(name);
            p.setLastName(getName());
            p.setCompanyNumber(1L);
            p.setSex(rand.nextBoolean() ? Person.Sex.MALE : Person.Sex.FEMALE);
            return orm.create(p);
    }

    private Cat makeCat(Orm orm, Person person) throws OrmException {
        Cat cat = new Cat();
        cat.setPersonNumber(person.getPersonNumber());
        cat.setType(rand.nextBoolean() ? Type.INDOOR : Type.OUTDOOR);
        cat.setAge(rand.nextInt(10));
        cat.setName(getName());
        return orm.create(cat);
    }

    private Dog makeDog(Orm orm, Person person) throws OrmException {
        Dog dog = new Dog();
        dog.setPersonNumber(person.getPersonNumber());
        dog.setAge(rand.nextInt(10));
        dog.setName(getName());
        return orm.create(dog);
    }

    private Bird makeBird(Orm orm, Person person) throws OrmException {
        Bird bird = new Bird();
        bird.setPersonNumber(person.getPersonNumber());
        bird.setAge(rand.nextInt(10));
        bird.setName(getName());
        bird.setKind(rand.nextBoolean() ? Kind.FREERANGE : Kind.CAGED);
        bird.setSingTime(Duration.ofMinutes(rand.nextInt(60)));
        return orm.create(bird);
    }

    private Fish makeFish(Orm orm, Person person) throws OrmException {
        Fish fish = new Fish();
        fish.setName(getName());
        return orm.create(fish);
    }

}
