package com.heliorm.sql;


import com.heliorm.OrmException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import test.persons.Person;
import test.pets.Cat;
import test.place.Province;
import test.place.Town;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.heliorm.sql.TestData.makeCat;
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
    private static List<Town> towns;

    @BeforeAll
    public static void setupData() throws OrmException {
        provinces = createAll(makeProvinces());
        towns = createAll(makeTowns(provinces));
        persons = createAll(makePersons(towns));
    }

    @Test
    public void testCreate() throws Exception {
        say("Testing create with auto-number Long key");
        Person person = orm().select(PERSON).where(PERSON.id.eq(persons.get(0).getId())).one();
        Cat cat = makeCat(person);
        Cat saved = orm().create(cat);
        cats.add(saved);    
        assertNotNull(saved, "The object returned by create should not be null");
        assertTrue(cat.getId() == null, "The id of the new object must be null before create");
        assertTrue(saved.getId() != null, "The id of the new object must be not-null after create");
        assertTrue(pojoCompareExcludingKey(cat, saved), "The new and created objects must be the same apart from the key");
    }

    @Test
    public void testUpdateWithNullKey() throws Exception {
        say("Testing update with null key");
        Long id = persons.get(0).getId();
        Person person = orm().select(PERSON).where(PERSON.id.eq(id)).one();
        person.setId(null);
        boolean failed = false;
        try {
            Person updated = orm().update(person);
        }
        catch (OrmException ex) {
            failed  = true;
        }
        assertTrue(failed, "The update must fail");
    }




    @Test
    public void testDelete() throws Exception {
        say("Testing delete with Long key");
        Cat cat = cats.remove(0);
        orm().delete(cat);
        Optional<Cat> opt = orm().select(CAT).where(CAT.id.eq(cat.getId())).optional();
        assertFalse(opt.isPresent(), "No object for delete id must be in the database");
    }


    @AfterAll
    public static void removeData() throws OrmException {
        deleteAll(Cat.class);
        deleteAll(Person.class);
        deleteAll(Town.class);
        deleteAll(Province.class);
    }
}
