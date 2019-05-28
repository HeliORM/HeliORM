package test;

import core.Person;
import static core.Tables.PERSON;
import java.sql.DriverManager;
import java.util.List;
import me.legrange.orm.Orm;
import static pojos.Tables.OBJ;

/**
 *
 * @author gideon
 */
public class test {

    public static void main(String... args) throws Exception {
        Orm orm = Orm.open(DriverManager.getConnection("jdbc:mysql://localhost:3306/orm?user=root"), Orm.Driver.MYSQL);

        orm.select(OBJ).where(OBJ.version.gt(1)).list();
        orm.select(PERSON).stream().forEach(System.out::println);
        List<Person> list = orm.select(PERSON)
                .where(PERSON.version.ge(1))
                .orderBy(PERSON.lastName).thenByDesc(PERSON.firstName)
                .list();
//        list.forEach(System.out::println);
//        List<Obj> objs = orm.select(OBJ).list();
//        objs.forEach(System.out::println);
    }

}
