package com.heliorm.sql;


import com.heliorm.OrmException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import test.persons.Person;
import test.pets.Bird;
import test.pets.Cat;
import test.pets.Dog;
import test.pets.Pet;
import test.place.Province;
import test.place.Town;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.heliorm.sql.TestData.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static test.Tables.*;

public class LazyLoadTest extends AbstractOrmTest {

    private static List<Person> persons;
    private static List<Cat> cats = new ArrayList<>();
    private static List<Dog> dogs = new ArrayList<>();
    private static List<Bird> birds = new ArrayList<>();
    private static List<Province> provinces;
    private static List<Town> towns;

    @BeforeAll
    public static void setupData() throws OrmException {
        provinces = createAll(makeProvinces());
        towns = createAll(makeTowns(provinces));
        persons = createAll(makePersons(towns));
        cats = createAll(makeCats(persons.size() * 5, persons));
        dogs = createAll(makeDogs(persons.size() * 4, persons));
        birds = createAll(makeBirds(persons.size() * 2, persons));

    }

    @Test
    public void loadWithNestedList() throws Exception {
        say("Testing load of nested list");
        Long id = persons.get(0).getId();
        Person person = orm().select(PERSON).where(PERSON.id.eq(id)).one();
        List<Pet> nested = person.getPets();
        List<Pet> loaded = orm().select(PET).where(PET.personId.eq(id)).list();
        assertTrue(nested != null, "Nested data should not be null");
        assertTrue(!nested.isEmpty(), "Nested data should not be empty");
        assertTrue(listCompareOrdered(nested, loaded), "Nested data should be same as loaded data");
    }

    @Test
    public void loadWithNestedSet() throws Exception {
        say("Testing load of nested set");
        Long id = provinces.get(0).getProvinceId();
        Province province = orm().select(PROVINCE).where(PROVINCE.provinceId.eq(id)).one();
        Set<Town> nested = province.getTowns();
        Set<Town> loaded = new HashSet(orm().select(TOWN).where(TOWN.provinceId.eq(id)).list());
        assertTrue(nested != null, "Nested data should not be null");
        assertTrue(!nested.isEmpty(), "Nested data should not be empty");
        assertTrue(setCompareAsIs(nested, loaded), "Nested data should be same as loaded data");
    }

    @AfterAll
    public static void removeData() throws OrmException {
        deleteAll(Cat.class);
        deleteAll(Dog.class);
        deleteAll(Bird.class);
        deleteAll(Person.class);
        deleteAll(Town.class);
        deleteAll(Province.class);
    }
}
