package test.persons;

import com.heliorm.annotation.Decimal;
import com.heliorm.annotation.ForeignKey;
import com.heliorm.annotation.Pojo;
import com.heliorm.annotation.PrimaryKey;
import com.heliorm.annotation.Text;
import test.place.Town;

/**
 * @author gideon
 */
@Pojo
public class Person {


    @PrimaryKey
    private Long id;
    @ForeignKey(pojo = Town.class)
    private Long townId;
    @Text(length = 30)
    private String firstName;
    @Text(length = 30, nullable = true)
    private String lastName;
    @Text(length = 128, nullable = true)
    private String emailAddress;
    @Decimal(nullable = true)
    private Double income;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getTownId() {
        return townId;
    }

    public void setTownId(Long townId) {
        this.townId = townId;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", townId=" + townId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                '}';
    }
}
