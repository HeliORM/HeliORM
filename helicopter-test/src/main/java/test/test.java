package test;

import java.sql.DriverManager;
import java.util.List;
import me.legrange.orm.Orm;
import pojos.Person;
import static test.Tables.PERSON;

/**
 *
 * @author gideon
 */
public class test {

    /*
                            .where(PERSON.lastName.eq("Le Grange")
                                .and(PERSON.firstName.eq("Gideon")))
                        .or(PERSON.lastName.eq("Smith")
                                .and(PERSON.firstName.eq("John")))

     */
    public static void main(String... args) throws Exception {
        Orm orm = Orm.open(DriverManager.getConnection("jdbc:mysql://localhost:3306/orm?user=root"), Orm.Driver.MYSQL);

        List<Person> list = orm.select(PERSON)
                .orderBy(PERSON.lastName).thenByDesc(PERSON.firstName)
                .list();
        list.forEach(System.out::println);
    }

}
