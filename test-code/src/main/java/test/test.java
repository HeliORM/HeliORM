package test;

import Tables.COMPANY;
import java.sql.DriverManager;
import java.util.Date;
import me.legrange.orm.Orm;
import pojos.Company;

/**
 *
 * @author gideon
 */
public class test {

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
