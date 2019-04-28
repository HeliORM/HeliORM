package test;

import java.util.List;
import me.legrange.orm.Orm;
import pojos.Person;
import static test.Tables.PERSON;

/**
 *
 * @author gideon
 */
public class test {

    public static void main(String... args) throws Exception {
        Orm orm = Orm.open(null, Orm.Driver.MYSQL);

//        List<Person> p = orm.select(PERSON).list();
        //   List<Person> persons1 = orm.select(PERSON).orderBy(PERSON.emailAddress).list();
        List<Person> list = orm.select(PERSON)
                .where(PERSON.lastName.eq("Le Grange")
                        .and(PERSON.firstName.eq("Gideon"))
                //                .or(PERSON.lastName.eq("Smith")
                //                        .and(PERSON.firstName.eq("John")))
                //                .join(COMPANY)
                //                .on(PERSON.companyNumber, COMPANY.companyNumber)
                //                .where(COMPANY.name.like("ACME%")
                //                        .or(COMPANY.name.like("FOOBAR%")))
                ).list();
//        List<Person> list = orm.select(PERSON)
//                .where(PERSON.lastName.eq("Le Grange")
//                        .and(PERSON.firstName.eq("Gideon")))
//                .or(PERSON.lastName.eq("Smith")
//                        .and(PERSON.firstName.eq("John")))
//                .join(COMPANY)
//                .on(PERSON.companyNumber, COMPANY.companyNumber)
//                .where(COMPANY.name.like("ACME%")
//                        .or(COMPANY.name.like("FOOBAR%")))
//                .list();

//        Ordered<Tables.PersonTable, Person> query1 = orm.select(PERSON)
//                .where(PERSON.emailAddress.in("joe@acme.com", "bob@acme.com"))
//                .and(PERSON.sex.notEq(Person.Sex.MALE))
//                .join(COMPANY).on(PERSON.companyNumber, COMPANY.companyNumber)
//                .where(COMPANY.name.eq("ACME"))
//                .orderBy(PERSON.emailAddress);
//        query1.list();
    }

}
