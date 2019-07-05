package test.persons;

import test.persons.Company;
import net.legrange.orm.annotation.Column;
import net.legrange.orm.annotation.ForeignKey;
import net.legrange.orm.annotation.Pojo;
import net.legrange.orm.annotation.PrimaryKey;
import pojos.Obj;

/**
 *
 * @author gideon
 */
@Pojo
public class Person extends Obj {

    public enum Sex {
        MALE, FEMALE, CONFUSED;
    }

    @PrimaryKey
    private Long personNumber;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String emailAddress;
    @ForeignKey(pojo = Company.class)
    private Long companyNumber;
    private Sex sex;

    public Long getPersonNumber() {
        return personNumber;
    }

    public void setPersonNumber(Long personNumber) {
        this.personNumber = personNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "Person{" + "personNumber=" + personNumber + ", firstName=" + firstName + ", lastName=" + lastName + ", emailAddress=" + emailAddress + ", companyNumber=" + companyNumber + ", sex=" + sex + '}';
    }

}
