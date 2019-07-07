package test;

import java.sql.Connection;
import java.sql.DriverManager;
import net.legrange.orm.Orm;
import net.legrange.orm.OrmException;
import net.legrange.orm.Table;
import static test.Tables.CAT;
import static test.Tables.PERSON;
import static test.Tables.PET;
import static test.Tables.TEST;
import test.pets.Cat;

/**
 *
 * @author gideon
 */
public class test {

    public static void main(String... args) throws Exception {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?user=root");
        Orm orm = Orm.open(con, Orm.Dialect.MYSQL);
        test t = new test();
        t.test10(orm);
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
        System.out.printf("Cat table has %s and %s\n", tableFor.getObjectClass(), tableFor.getFields());
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
        cat.setName("Cool Cat");
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
        orm.select(PET).join(PERSON).on(PET.personNumber, PERSON.personNumber).where(PERSON.firstName.eq("John")).list().forEach(System.out::println);
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
