package com.heliorm.sql;


import com.heliorm.OrmException;
import com.heliorm.test.persons.Person;
import com.heliorm.test.pets.Cat;
import com.heliorm.test.pets.CatBreed;
import com.heliorm.test.place.Province;
import com.heliorm.test.place.Town;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.heliorm.sql.TestData.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AutoNumberTest extends AbstractOrmTest {


    private static List<Person> persons;
    private static List<CatBreed> catBreeds;

    @BeforeAll
    public static void setupData() throws OrmException {
        catBreeds = createAll(makeCatBreeds());
        List<Province> provinces = createAll(makeProvinces());
        List<Town> towns = createAll(makeTowns(provinces));
        persons = createAll(makePersons(towns));
    }

    @AfterAll
    public static void removeData() throws OrmException {
        deleteAll(Cat.class);
        deleteAll(Person.class);
        deleteAll(Town.class);
        deleteAll(Province.class);
    }

    @Test
    public void testCreateWithLongKey() throws Exception {
        say("Testing create with auto-number Long key");
        Person person = persons.getFirst();
        Cat cat = makeCat(person, catBreeds.getFirst());
        Cat saved = orm().create(cat);
        assertNotNull(saved, "The object returned by create should not be null");
        assertTrue(cat.getId() == null, "The id of the new object must be null before create");
        assertTrue(saved.getId() != null, "The id of the new object must be not-null after create");
        assertTrue(pojoCompareExcludingKey(cat, saved), "The new and created objects must be the same apart from the key");
    }
}
