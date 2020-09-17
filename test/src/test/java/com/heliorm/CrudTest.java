package com.heliorm;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import test.persons.Person;
import test.pets.Cat;
import test.pets.Dog;
import test.pets.Mamal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.heliorm.TestData.makeCat;
import static com.heliorm.TestData.makeCats;
import static com.heliorm.TestData.makeDogs;
import static com.heliorm.TestData.makePersons;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static test.Tables.CAT;
import static test.Tables.MAMAL;
import static test.Tables.PERSON;

public class CrudTest extends AbstractOrmTest {

    private static final int MAX_PERSONS = 3;
    private static List<Person> persons;

    @BeforeAll
    public static void setupData() throws OrmException {
        persons = createAll(makePersons(MAX_PERSONS));
    }


    @Test
    public void testCreate() throws Exception {
        say("Testing create with auto-number Long key");
        Person person = persons.get(0);
        Cat cat = makeCat(person);
        Cat saved = orm.create(cat);
        assertNotNull(saved, "The object returned by create should not be null");
        assertTrue(cat.getId() == null, "The id of the new object must be null before create");
        assertTrue(saved.getId() != null, "The id of the new object must be not-null after create");
        assertTrue(pojoCompareExcludingKey(cat, saved), "The new and created objects must be the same apart from the key");
    }

}
