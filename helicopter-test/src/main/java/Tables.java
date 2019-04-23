
import me.legrange.orm.AbstractField;
import me.legrange.orm.DateField;
import me.legrange.orm.NumberField;
import me.legrange.orm.StringField;
import me.legrange.orm.Table;
import pojos.Company;
import pojos.Person;

public final class Tables {

    public static final class CompanyTable implements Table<Company> {

        public final DateField<CompanyTable, Company> created = new AbstractField(Company.class, "created", "created");
        public final DateField<CompanyTable, Company> modified = new DateField(Company.class, "modified", "modified");
        public final NumberField<CompanyTable, Company, Integer> version = new NumberField(Company.class, "version", "version");
        public final NumberField<CompanyTable, Company, Long> companyNumber = new NumberField(Company.class, "companyNumber", "companyNumber");
        public final StringField<CompanyTable, Company> name = new StringField(Company.class, "name", "name");

        public List<Field> getFields() {
            return Arrays.asList(created, modified, version, companyNumber, name);
        }

        public String getSqlTable() {
            return "Company";
        }

        public Class<Company> getObjectClass() {
            return Company.class;
        }

    }

    public static final class PersonTable implements Table<Person> {

        public final DateField<PersonTable, Person> created = new DateField(Person.class, "created", "created");
        public final DateField<PersonTable, Person> modified = new DateField(Person.class, "modified", "modified");
        public final NumberField<PersonTable, Person, Integer> version = new NumberField(Person.class, "version", "version");
        public final NumberField<PersonTable, Person, Long> personNumber = new NumberField(Person.class, "personNumber", "personNumber");
        public final StringField<PersonTable, Person> firstName = new StringField(Person.class, "firstName", "firstName");
        public final StringField<PersonTable, Person> lastName = new StringField(Person.class, "lastName", "lastName");
        public final StringField<PersonTable, Person> emailAddress = new StringField(Person.class, "emailAddress", "emailAddress");
        public final NumberField<PersonTable, Person, Long> companyNumber = new NumberField(Person.class, "companyNumber", "companyNumber");

        public List<Field> getFields() {
            return Arrays.asList(created, modified, version, personNumber, firstName, lastName, emailAddress, companyNumber);
        }

        public String getSqlTable() {
            return "Person";
        }

        public Class<Person> getObjectClass() {
            return Person.class;
        }

    }

    public static final CompanyTable COMPANY = new CompanyTable();
    public static final PersonTable PERSON = new PersonTable();
}
