package test.persons;

import com.heliorm.annotation.*;
import test.pets.Pet;
import test.place.Town;

import java.util.List;

/**
 * @author gideon
 */
@Pojo
public class Person {


    @PrimaryKey
    private Long id;
    @ForeignKey(pojo = Town.class)
    private Long townId;
    @Column
    private String firstName;
    @Column(nullable = true)
    private String lastName;
    @Column
    private String emailAddress;
    @Column(nullable = true)
    private Double income;

    @Collection(pojo = Pet.class)
    private List<Pet> pets;

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

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
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
