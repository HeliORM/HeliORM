package test;

import me.legrange.orm.Orm;
import pojos.Company;
import static test.Tables.COMPANY;
import static test.Tables.PERSON;

/**
 *
 * @author gideon
 */
public class test {

    public static void main(String... args) throws Exception {
        Orm orm = null;
        Company company = orm.select(COMPANY)
                .where(COMPANY.name).eq("Adept").one();
        orm.select(PERSON)
                .where(PERSON.emailAddress).in("joe@acme.com", "bob@acme.com")
                .join(COMPANY).on(PERSON.companyNumber, COMPANY.companyNumber)
                .where(COMPANY.name).eq("Adept")
                .orderBy(PERSON.emailAddress)
                .list();
    }

}
