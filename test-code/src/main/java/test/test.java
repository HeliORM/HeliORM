package test;

import core.Cat;
import static core.Tables.CAT;
import static core.Tables.PERSON;
import static core.Tables.PET;
import java.sql.DriverManager;
import me.legrange.orm.Orm;
import me.legrange.orm.OrmException;

/**
 *
 * @author gideon
 */
public class test {

    public static void main(String... args) throws Exception {
        Orm orm = Orm.open(DriverManager.getConnection("jdbc:mysql://localhost:3306/orm?user=root"), Orm.Driver.MYSQL);
        test t = new test();
        t.test8(orm);
    }

    private void test8(Orm orm) throws OrmException {
        System.out.println("-- Delete Cat ---");
        Cat cat = orm.select(CAT).where(CAT.catNumber.eq(4)).one();
        orm.delete(CAT, cat);
        System.out.println("Cat deleted");
    }

    private void test7(Orm orm) throws OrmException {
        System.out.println("-- Update Cat ---");
        Cat cat = orm.select(CAT).where(CAT.catNumber.eq(5L)).one();
        cat.setName("Chilli");
        cat = orm.update(CAT, cat);
        System.out.println("Cat updated with key " + cat.getCatNumber());
    }

    private void test6(Orm orm) throws OrmException {
        System.out.println("-- Create Cat ---");
        Cat cat = new Cat();
        cat.setName("Marmite");
        cat.setAge(1);
        cat.setPersonNumber(1L);
        cat = orm.create(CAT, cat);
        System.out.println("Cat created with key " + cat.getCatNumber());
    }

    private void test2(Orm orm) throws OrmException {
        System.out.println("-- list all person ---");
        orm.select(PERSON).list().forEach(System.out::println);
    }

    private void test1(Orm orm) throws OrmException {
        System.out.println("-- stream all person ---");
        orm.select(PERSON).stream().forEach(System.out::println);
    }

    private void test3(Orm orm) throws OrmException {
        System.out.println("-- oneOrNone person ---");
        orm.select(PERSON).where(PERSON.personNumber.eq(1L)).oneOrNone().ifPresent(System.out::println);
    }

    private void test4(Orm orm) throws OrmException {
        orm.select(PET).join(PERSON).on(PET.personNumber, PERSON.personNumber).where(PERSON.firstName.eq("John")).list().forEach(System.out::println);
    }

    private void test5(Orm orm) throws OrmException {
        orm.select(PERSON)
                .join(PET).on(PERSON.personNumber, PET.personNumber)
                .where(PET.age.eq(3))
                .orderByDesc(PERSON.modified)
                .list().forEach(System.out::println);
    }
}
