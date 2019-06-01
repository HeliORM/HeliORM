package test;

import static core.Tables.PERSON;
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
        t.test3(orm);
//        t.test1(orm);
//        t.test2(orm);
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
        System.out.println("-- oneOrNone all person ---");
        orm.select(PERSON).where(PERSON.personNumber.eq(1L)).oneOrNone().ifPresent(System.out::println);
    }

}
