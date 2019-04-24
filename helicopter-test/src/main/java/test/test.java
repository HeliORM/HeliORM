package test;

import java.util.List;
import java.util.stream.Stream;
import me.legrange.orm.Ordered;
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

        List<Person> persons1 = orm.select(PERSON).orderBy(PERSON.emailAddress).list();
        List<Person> list = orm.select(PERSON)
                .where(PERSON.lastName.eq("Le Grange").and(PERSON.firstName.notEq("Gideon")))
                .or(PERSON.lastName.eq("Smith").and(PERSON.firstName.eq("John")))
                .join(COMPANY).on(PERSON.companyNumber, COMPANY.companyNumber).list();
        //                .where(PERSON.emailAddress).in("joe@acme.com", "bob@acme.com")
        //                .join(COMPANY).on(PERSON.companyNumber, COMPANY.companyNumber)
        //                .where(COMPANY.name).eq("ACME")
        //                .orderBy(PERSON.emailAddress)
        //                .list();
        //
        Ordered<Tables.PersonTable, Person> query1 = orm.select(PERSON)
                .where(PERSON.emailAddress.in("joe@acme.com", "bob@acme.com"))
                .and(PERSON.sex.notEq(Person.Sex.MALE))
                .join(COMPANY).on(PERSON.companyNumber, COMPANY.companyNumber)
                .where(COMPANY.name.eq("ACME"))
                .orderBy(PERSON.emailAddress);
        Stream<Person> stream1 = query1.stream();
    }

}
