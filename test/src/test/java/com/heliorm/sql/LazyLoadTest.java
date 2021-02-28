package com.heliorm.sql;


import com.heliorm.OrmException;
import com.heliorm.collection.LazyLoadedList;
import com.heliorm.collection.LazyLoadedSet;
import com.heliorm.impl.Part;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static test.Tables.PET;

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
        birds = createAll(makeBirds(persons.size()*2, persons));

    }

    @Test
    public void lazyLoadListTest() throws Exception {
        Person person = persons.get(0);
        List<Pet> control = orm().select(PET).where(PET.personId.eq(person.getId())).list();
        List<Pet> petz = new LazyLoadedList((Part) orm().select(PET).where(PET.personId.eq(person.getId())), orm().selector());
        assertFalse(petz.isEmpty(), "Pets list should not be empty");
        assertTrue(listCompareAsIs(petz, control), "Lazy loaded data should be the same as normally loaded");
    }

    @Test
    public void lazyLoadSetTest() throws Exception {
        Person person = persons.get(0);
        Set<Pet> control = new HashSet(orm().select(PET).where(PET.personId.eq(person.getId())).list());
        Set<Pet> petz = new LazyLoadedSet((Part) orm().select(PET).where(PET.personId.eq(person.getId())), orm().selector());
        assertFalse(petz.isEmpty(), "Pets list should not be empty");
        assertTrue(setCompareAsIs(petz, control), "Lazy loaded data should be the same as normally loaded");
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
