package test;

import java.util.List;
import me.legrange.orm.Orm;
import pojos.Person;
import static test.Tables.COMPANY;
import static test.Tables.PERSON;

/**
 *
 * @author gideon
 */
public class test {

    public static void main(String... args) throws Exception {
        Orm orm = null;

        List<Person> persons0 = orm.select(PERSON).orderBy(PERSON.emailAddress).list();
        List<Person> persons1 = orm.select(PERSON).orderBy(PERSON.emailAddress).list();
        List<Person> persons2 = orm.select(PERSON)
                .where(PERSON.emailAddress).in("joe@acme.com", "bob@acme.com")
                .join(COMPANY).on(PERSON.companyNumber, COMPANY.companyNumber)
                .where(COMPANY.name).eq("Adept")
                .orderBy(PERSON.emailAddress)
                .list();
    }

}
