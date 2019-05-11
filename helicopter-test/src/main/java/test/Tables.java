package test;

import java.util.Arrays;
import java.util.List;
import me.legrange.orm.DateField;
import me.legrange.orm.EnumField;
import me.legrange.orm.Field;
import me.legrange.orm.NumberField;
import me.legrange.orm.StringField;
import me.legrange.orm.Table;
import me.legrange.orm.impl.DateFieldPart;
import me.legrange.orm.impl.EnumFieldPart;
import me.legrange.orm.impl.IntegerFieldPart;
import me.legrange.orm.impl.LongFieldPart;
import me.legrange.orm.impl.StringFieldPart;
import pojos.Company;
import pojos.Person;
import pojos.Person.Sex;

public final class Tables {

    public static final class CompanyTable implements Table<Company> {

        public final DateField<CompanyTable, Company> created = new DateFieldPart("created", "created") {
        };
        public final DateField<CompanyTable, Company> modified = new DateFieldPart("modified", "modified");
        public final NumberField<CompanyTable, Company, Integer> version = new IntegerFieldPart("version", "version");
        public final NumberField<CompanyTable, Company, Long> companyNumber = new LongFieldPart("companyNumber", "companyNumber");
        public final StringField<CompanyTable, Company> name = new StringFieldPart("name", "name");

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

        public final DateField<PersonTable, Person> created = new DateFieldPart("created", "created");
        public final DateField<PersonTable, Person> modified = new DateFieldPart("modified", "modified");
        public final NumberField<PersonTable, Person, Integer> version = new IntegerFieldPart("version", "version");
        public final NumberField<PersonTable, Person, Long> personNumber = new LongFieldPart("personNumber", "personNumber");
        public final StringField<PersonTable, Person> firstName = new StringFieldPart("firstName", "firstName");
        public final StringField<PersonTable, Person> lastName = new StringFieldPart("lastName", "lastName");
        public final StringField<PersonTable, Person> emailAddress = new StringFieldPart("emailAddress", "emailAddress");
        public final NumberField<PersonTable, Person, Long> companyNumber = new LongFieldPart("companyNumber", "companyNumber");
        public final EnumField<PersonTable, Person, Sex> sex = new EnumFieldPart(Sex.class, "sex", "sex");

        public List<Field> getFields() {
            return Arrays.asList(created, modified, version, personNumber, firstName, lastName, emailAddress, companyNumber, sex);
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
