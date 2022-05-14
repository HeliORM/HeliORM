package com.heliorm.sql;


import com.heliorm.OrmException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import test.persons.Person;
import test.pets.Cat;
import test.pets.CatBreed;
import test.place.Province;
import test.place.Town;

import java.util.ArrayList;
import java.util.List;

import static com.heliorm.sql.TestData.makeCat;
import static com.heliorm.sql.TestData.makeCatBreeds;
import static com.heliorm.sql.TestData.makePersons;
import static com.heliorm.sql.TestData.makeProvinces;
import static com.heliorm.sql.TestData.makeTowns;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AutoNumberTest extends AbstractOrmTest {

    private static final int MAX_PERSONS = 3;

    private static List<Province> provinces;
    private static List<Town> towns;
    private static List<Person> persons;
    private static List<CatBreed> catBreeds;
    private static List<Cat> cats = new ArrayList<>();

    @BeforeAll
    public static void setupData() throws OrmException {
        catBreeds = createAll(makeCatBreeds());
        provinces = createAll(makeProvinces());
        towns = createAll(makeTowns(provinces));
        persons = createAll(makePersons(towns));
    }

    @AfterAll
    public static void removeData() throws OrmException {
        deleteAll(Cat.class);
        deleteAll(Person.class);
        deleteAll(Town.class);
        deleteAll(Province.class);
    }

//    @Test
//    public void testCreateWithStringKey() throws Exception {
//        say("Testing create with auto-number String key");
//        Town town = new Town();
//        town.setName("Somerset West");
//        town.setProvinceId(0L);
//        Town saved = orm().create(town);
//        assertNotNull(saved, "The object returned by create should not be null");
//        assertTrue(town.getId() == null, "The id of the new object must be null before create");
//        assertTrue(saved.getId() != null, "The id of the new object must be not-null after create");
//        assertTrue(pojoCompareExcludingKey(town, saved), "The new and created objects must be the same apart from the key");
//    }

    @Test
    public void testCreateWithLongKey() throws Exception {
        say("Testing create with auto-number Long key");
        Person person = persons.get(0);
        Cat cat = makeCat(person, catBreeds.get(0));
        Cat saved = orm().create(cat);
        cats.add(saved);
        assertNotNull(saved, "The object returned by create should not be null");
        assertTrue(cat.getId() == null, "The id of the new object must be null before create");
        assertTrue(saved.getId() != null, "The id of the new object must be not-null after create");
        assertTrue(pojoCompareExcludingKey(cat, saved), "The new and created objects must be the same apart from the key");
    }
}
