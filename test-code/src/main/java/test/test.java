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
        t.test1(orm);
        t.test2(orm);

//        orm.select(OBJ).where(OBJ.version.gt(1)).list();
//        orm.select(PERSON).stream().forEach(System.out::println);
//                .where(PERSON.version.ge(1))
//                .orderBy(PERSON.lastName).thenByDesc(PERSON.firstName)
//                .list();
//        list.forEach(System.out::println);
//        List<Obj> objs = orm.select(OBJ).list();
//        objs.forEach(System.out::println);
    }

    private void test1(Orm orm) throws OrmException {
        System.out.println("-- list all person ---");
        orm.select(PERSON).list().forEach(System.out::println);
    }

    private void test2(Orm orm) throws OrmException {
        System.out.println("-- stream all person ---");
        orm.select(PERSON).stream().forEach(System.out::println);
    }

}
