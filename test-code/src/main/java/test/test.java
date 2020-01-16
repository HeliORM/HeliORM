package test;

import net.legrange.orm.Orm;
import net.legrange.orm.OrmBuilder;
import net.legrange.orm.OrmException;
import net.legrange.orm.OrmTransaction;
import net.legrange.orm.Table;
import test.pets.Bird;
import test.pets.Bird.Kind;
import test.pets.Cat;
import test.pets.Cat.Type;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Duration;
import java.util.List;

import static test.Tables.BIRD;
import static test.Tables.CAT;
import static test.Tables.PERSON;
import static test.Tables.PET;
import static test.Tables.TEST;

/**
 * @author gideon
 */
public class test {

    public static void main(String... args) throws Exception {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/?user=root");
//        Orm orm = Orm.open(con, Orm.Dialect.MYSQL);
        Orm orm = OrmBuilder.create(con)
                .setDialect(Orm.Dialect.MYSQL)
                .setRollbackOnUncommittedClose(false)
//                .withPojoOperations(new BeanPojoOperations())
                .mapDatabase(TEST, "petz").build();
        test t = new test();
//        t.test1(orm);
//        t.test2(orm);
//        t.test3(orm);
        t.test4(orm);
//        t.test5(orm);
//        t.test6(orm);
//        t.test7(orm);
//        t.test8(orm);
    }


    /**
     * Transaction - test rollback if you forget the commit
     */
    private void test15(Orm orm) throws OrmException {
        System.out.println("-- Create Bird ---");
        Bird bird = new Bird();
        bird.setName("Bigger Bird");
        bird.setAge(2);
        bird.setKind(Kind.FREERANGE);
        bird.setPersonNumber(1L);
        bird.setSingTime(Duration.ofMinutes(12));
        try (OrmTransaction tx = orm.openTransaction()) {
            bird = orm.create(bird);
            tx.commit();
        } catch (OrmException ex) {
            System.out.printf("Creating bird failed: %s\n", ex.getMessage());
        }
        System.out.println("Bird created with key " + bird.getBirdId());

        bird.setName("Mofo big bird yo");
        try (OrmTransaction tx = orm.openTransaction()) {
            bird = orm.create(bird);
            tx.commit();
        } catch (OrmException ex) {
            System.out.printf("Creating bird failed: %s\n", ex.getMessage());
            ex.printStackTrace();
        }
        System.out.println("Bird created with key " + bird.getBirdId());

    }

    /**
     * Transaction - test rollback
     */
    private void test14(Orm orm) throws OrmException {
        System.out.println("-- Create Bird ---");
        Bird bird = new Bird();
        bird.setName("Bigger Bird");
        bird.setAge(2);
        bird.setKind(Kind.FREERANGE);
        bird.setPersonNumber(1L);
        bird.setSingTime(Duration.ofMinutes(12));
        try (OrmTransaction tx = orm.openTransaction()) {
            bird = orm.create(bird);
            tx.rollback();
        } catch (OrmException ex) {
            System.out.printf("Creating bird failed: %s\n", ex.getMessage());
        }
        System.out.println("Bird created with key " + bird.getBirdId());
    }

    /**
     * Transaction - test commit
     */
    private void test13(Orm orm) throws OrmException {
        System.out.println("-- Create Bird ---");
        Bird bird = new Bird();
        bird.setName("Big Bird");
        bird.setAge(2);
        bird.setKind(Kind.FREERANGE);
        bird.setPersonNumber(1L);
        bird.setSingTime(Duration.ofMinutes(12));
        try (OrmTransaction tx = orm.openTransaction()) {
            bird = orm.create(bird);
            tx.commit();
        } catch (OrmException ex) {
            System.out.printf("Creating bird failed: %s\n", ex.getMessage());
        }
        System.out.println("Bird created with key " + bird.getBirdId());
    }


    /**
     * Read bird and look at it's sing time (test duration)
     */
    private void test12(Orm orm) throws OrmException {
        System.out.println("-- Load Birds for person 1 ---");
        List<Bird> birds = orm.select(BIRD).where(BIRD.personNumber.eq(1)).list();
        for (Bird bird : birds) {
            double secs = bird.getSingTime().getSeconds();
            System.out.printf("%s sings for %s seconds (%s minutes)\n", bird.getName(), secs, secs / 60);
        }
    }

    /**
     * Create a bird for person #1
     */
    private void test11(Orm orm) throws OrmException {
        System.out.println("-- Create Bird ---");
        Bird bird = new Bird();
        bird.setName("Polly Wants Crack");
        bird.setAge(3);
        bird.setKind(Kind.FREERANGE);
        bird.setPersonNumber(1L);
        bird.setSingTime(Duration.ofMinutes(90)); // Annoying AF
        bird = orm.create(bird);
        System.out.println("Bird created with key " + bird.getBirdId());
    }

    /**
     * Injection test
     */
    private void test10(Orm orm) throws OrmException {
        System.out.println("-- Kitty drop tables ---");
        orm.select(TEST.CAT).where(TEST.CAT.name.eq("Kitty'; DROP table cat;--")).list();
    }

    /**
     * Test table lookup
     */
    private void test9(Orm orm) throws OrmException, ClassNotFoundException {
        System.out.println("-- See if we have table ---");
        Table<Cat> tableFor = orm.tableFor(new Cat());
        System.out.printf("Cat table has %s with fields %s\n", tableFor.getObjectClass(), tableFor.getFields());
    }

    /**
     * Select and delete cat #4
     */
    private void test8(Orm orm) throws OrmException {
        System.out.println("-- Delete Cat ---");
        Cat cat = orm.select(CAT).where(CAT.catNumber.eq(4)).one();
        orm.delete(cat);
        System.out.println("Cat deleted");
    }

    /**
     * Rename cat #5 to Death Cat and print it's key
     */
    private void test7(Orm orm) throws OrmException {
        System.out.println("-- Update Cat ---");
        Cat cat = orm.select(CAT).where(CAT.catNumber.eq(5)).one();
        cat.setName("Cooler Cat");
        cat = orm.update(cat);
        System.out.println("Cat updated with key " + cat.getCatNumber());
    }

    /**
     * Create a cat for person #1
     */
    private void test6(Orm orm) throws OrmException {
        System.out.println("-- Create Cat ---");
        Cat cat = new Cat();
        cat.setName("Supreme Cat");
        cat.setAge(1);
        cat.setType(Type.INDOOR);
        cat.setPersonNumber(1L);
        cat = orm.create(cat);
        System.out.println("Cat created with key " + cat.getCatNumber());
    }

    /**
     * List all persons
     */
    private void test2(Orm orm) throws OrmException {
        System.out.println("-- list all person ---");
        orm.select(PERSON).list().forEach(System.out::println);
    }

    /**
     * Stream all persons
     */
    private void test1(Orm orm) throws OrmException {
        System.out.println("-- stream all person ---");
        orm.select(PERSON).stream().forEach(System.out::println);
    }

    /**
     * Load person 1 if it exists
     */
    private void test3(Orm orm) throws OrmException {
        System.out.println("-- oneOrNone person ---");
        orm.select(PERSON).where(PERSON.personNumber.eq(1)).oneOrNone().ifPresent(System.out::println);
    }

    /**
     * Load all pets owned by a person called John
     */
    private void test4(Orm orm) throws OrmException {
        System.out.println("-- query on abstract class ---");
        orm.select(PET)
                .orderBy(PET.age).thenBy(PET.name)
                .list()
                .forEach(System.out::println);

        //.join(PERSON).on(PET.personNumber, PERSON.personNumber).where(PERSON.firstName.eq("John")).list().forEach(System.out::println);
    }

    /**
     * Load all persons who own a 3 year old pet and order by person's first
     * name
     */
    private void test5(Orm orm) throws OrmException {
        orm.select(PERSON)
                .join(PET).on(PERSON.personNumber, PET.personNumber)
                //                .where(PET.age.eq(3))
                .orderBy(PERSON.personNumber)
                .stream()
                .forEach(System.out::println);
    }
}
