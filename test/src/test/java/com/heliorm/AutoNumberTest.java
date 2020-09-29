package com.heliorm;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import test.persons.Person;
import test.pets.Cat;
import test.place.Province;
import test.place.Town;

import java.util.ArrayList;
import java.util.List;

import static com.heliorm.TestData.makeCat;
import static com.heliorm.TestData.makePersons;
import static com.heliorm.TestData.makeProvinces;
import static com.heliorm.TestData.makeTowns;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AutoNumberTest extends AbstractOrmTest {

    private static final int MAX_PERSONS = 3;

    private static List<Province> provinces;
    private static List<Town> towns;
    private static List<Person> persons;
    private static List<Cat> cats = new ArrayList<>();

    @BeforeAll
    public static void setupData() throws OrmException {
        provinces = createAll(makeProvinces());
        towns = createAll(makeTowns(provinces));
        persons = createAll(makePersons(towns));
    }


    @Test
    public void testCreateWithLongKey() throws Exception {
        say("Testing create with auto-number Long key");
        Person person = persons.get(0);
        Cat cat = makeCat(person);
        Cat saved = orm.create(cat);
        cats.add(saved);
        assertNotNull(saved, "The object returned by create should not be null");
        assertTrue(cat.getId() == null, "The id of the new object must be null before create");
        assertTrue(saved.getId() != null, "The id of the new object must be not-null after create");
        assertTrue(pojoCompareExcludingKey(cat, saved), "The new and created objects must be the same apart from the key");
    }

    @Test
    public void testCreateWithStringKey() throws Exception {
        say("Testing create with auto-number String key");
        Town town = new Town();
        town.setName("Somerset West");
        Town saved = orm.create(town);
        assertNotNull(saved, "The object returned by create should not be null");
        assertTrue(town.getId() == null, "The id of the new object must be null before create");
        assertTrue(saved.getId() != null, "The id of the new object must be not-null after create");
        assertTrue(pojoCompareExcludingKey(town, saved), "The new and created objects must be the same apart from the key");
    }


    @AfterAll
    public static void removeData() throws OrmException {
        deleteall(Cat.class);
        deleteall(Person.class);
        deleteall(Town.class);
        deleteall(Province.class);
    }
}
