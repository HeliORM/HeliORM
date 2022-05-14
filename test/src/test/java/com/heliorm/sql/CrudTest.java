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
import java.util.Optional;

import static com.heliorm.Query.where;
import static com.heliorm.sql.TestData.makeCat;
import static com.heliorm.sql.TestData.makeCatBreeds;
import static com.heliorm.sql.TestData.makePersons;
import static com.heliorm.sql.TestData.makeProvinces;
import static com.heliorm.sql.TestData.makeTowns;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static test.Tables.CAT;
import static test.Tables.PERSON;

public class CrudTest extends AbstractOrmTest {

    private static List<Person> persons;
    private static List<Cat> cats = new ArrayList<>();
    private static List<Province> provinces;
    private static List<CatBreed> catBreeds;
    private static List<Town> towns;

    @BeforeAll
    public static void setupData() throws OrmException {
        provinces = createAll(makeProvinces());
        towns = createAll(makeTowns(provinces));
        persons = createAll(makePersons(towns));
        catBreeds = createAll(makeCatBreeds());
    }

    @AfterAll
    public static void removeData() throws OrmException {
        deleteAll(Cat.class);
        deleteAll(Person.class);
        deleteAll(Town.class);
        deleteAll(Province.class);
    }

    @Test
    public void testCreate() throws Exception {
        say("Testing create with auto-number Long key");
        Person person = orm().select(PERSON, where(PERSON.id.eq(persons.get(0).getId()))).one();
        Cat cat = makeCat(person, catBreeds.get(0));
        Cat saved = orm().create(cat);
        cats.add(saved);
        assertNotNull(saved, "The object returned by create should not be null");
        assertTrue(cat.getId() == null, "The id of the new object must be null before create");
        assertTrue(saved.getId() != null, "The id of the new object must be not-null after create");
        assertTrue(pojoCompareExcludingKey(cat, saved), "The new and created objects must be the same apart from the key");
    }

    @Test
    public void testUpdate() throws Exception {
        say("Testing update with auto-number Long key");
        Long id = persons.get(0).getId();
        Person person = orm().select(PERSON, where(PERSON.id.eq(id))).one();
        String tmp = person.getFirstName();
        person.setFirstName(person.getLastName());
        person.setLastName(tmp);
        Person updated = orm().update(person);
        Person loaded = orm().select(PERSON, where(PERSON.id.eq(id))).one();
        assertNotNull(updated, "The object returned by update should not be null");
        assertTrue(pojoCompare(updated, loaded), "The updated and loaded objects must be the same");
        assertTrue(pojoCompare(updated, person), "The updated and modified objects must be the same");
        assertTrue(pojoCompare(loaded, person), "The loaded and modified objects must be the same");
    }

    @Test
    public void testUpdateWithNoChange() throws Exception {
        say("Testing update with auto-number Long key where nothing changes");
        Long id = persons.get(0).getId();
        Person person = orm().select(PERSON, where(PERSON.id.eq(id))).one();
        Person updated = orm().update(person);
        Person loaded = orm().select(PERSON, where(PERSON.id.eq(id))).one();
        assertNotNull(updated, "The object returned by update should not be null");
        assertTrue(pojoCompare(updated, loaded), "The updated and loaded objects must be the same");
        assertTrue(pojoCompare(updated, person), "The updated and modified objects must be the same");
        assertTrue(pojoCompare(loaded, person), "The loaded and modified objects must be the same");
    }

    @Test
    public void testUpdateWithNullKey() throws Exception {
        say("Testing update with null key");
        Long id = persons.get(0).getId();
        Person person = orm().select(PERSON, where(PERSON.id.eq(id))).one();
        person.setId(null);
        boolean failed = false;
        try {
            Person updated = orm().update(person);
        } catch (OrmException ex) {
            failed = true;
        }
        assertTrue(failed, "The update must fail");
    }

    @Test
    public void testUpdateWithBadKey() throws Exception {
        say("Testing update with bad key");
        Long id = persons.get(0).getId();
        Person person = orm().select(PERSON, where(PERSON.id.eq(id))).one();
        Long newKey = 10000000L;
        person.setId(newKey);
        boolean failed = false;
        try {
            Person updated = orm().update(person);
            Person loaded = orm().select(PERSON, where(PERSON.id.eq(newKey))).one();
        } catch (OrmException ex) {
            failed = true;
        }
        assertTrue(failed, "The update must fail");
    }

    @Test
    public void testDelete() throws Exception {
        say("Testing delete with Long key");
        Cat cat = cats.remove(0);
        orm().delete(cat);
        Optional<Cat> opt = orm().select(CAT, where(CAT.id.eq(cat.getId()))).optional();
        assertFalse(opt.isPresent(), "No object for delete id must be in the database");
    }
}
