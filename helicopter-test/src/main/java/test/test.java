package test;

import java.sql.DriverManager;
import java.util.Date;
import me.legrange.orm.Orm;
import pojos.Company;
import static test.Tables.COMPANY;

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

        Company comp = new Company();
        comp.setName("Stephan's Security Services");
        comp.setCreated(new Date());
        comp.setModified(new Date());
        comp.setVersion(1);
        orm.create(COMPANY, comp);
//        orm.select(PERSON).stream().forEach(System.out::println);
//        List<Person> list = orm.select(PERSON)
//                .orderBy(PERSON.lastName).thenByDesc(PERSON.firstName)
//                .list();
//        list.forEach(System.out::println);
    }

}
