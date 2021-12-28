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

import java.util.List;

import static com.heliorm.sql.TestData.makeCatBreeds;
import static com.heliorm.sql.TestData.makeCats;
import static com.heliorm.sql.TestData.makePersons;
import static com.heliorm.sql.TestData.makeProvinces;
import static com.heliorm.sql.TestData.makeTowns;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TableForTest extends AbstractOrmTest {

    private static List<Province> provinces;
    private static List<Town> towns;
    private static List<Person> persons;
    private static List<CatBreed> catBreeds;
    private static List<Cat> cats;


    @BeforeAll
    public static void setupData() throws OrmException {
        provinces = createAll(makeProvinces());
        towns = createAll(makeTowns(provinces));
        persons = createAll(makePersons(towns));
        catBreeds = createAll(makeCatBreeds());
        cats = createAll(makeCats(persons.size() * 5, persons, catBreeds));
    }


    @Test
    public void testTableForClass() throws Exception {
        say("Testing tableFor for class");
        List<?> all = orm().select(orm().tableFor(Cat.class)).list();
        assertTrue(all.stream().filter(o -> o instanceof  Cat).count() == all.size(), format("All the objects loaded are of type cat (%d)", all.size()));
    }

    @Test
    public void testTableForObject() throws Exception {
        say("Testing tableFor for object");
        List<?> all = orm().select(orm().tableFor(new Cat())).list();
        assertTrue(all.stream().filter(o -> o instanceof  Cat).count() == all.size(), format("All the objects loaded are of type cat (%d)", all.size()));
    }


    @AfterAll
    public static void removeData() throws OrmException {
        deleteAll(Cat.class);
        deleteAll(Person.class);
        deleteAll(Town.class);
        deleteAll(Province.class);
    }
}
