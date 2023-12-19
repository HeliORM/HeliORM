package com.heliorm.sql;


import com.heliorm.OrmException;
import com.heliorm.test.persons.Person;
import com.heliorm.test.pets.Cat;
import com.heliorm.test.place.Province;
import com.heliorm.test.place.Town;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TableForTest extends AbstractOrmTest {


    @BeforeAll
    public static void setupData() {
    }

    @AfterAll
    public static void removeData() throws OrmException {
        deleteAll(Cat.class);
        deleteAll(Person.class);
        deleteAll(Town.class);
        deleteAll(Province.class);
    }

    @Test
    public void testTableForClass() throws Exception {
        say("Testing tableFor for class");
        List<?> all = orm().select(orm().tableFor(Cat.class)).list();
        assertEquals(all.stream().filter(o -> o instanceof Cat).count(), all.size(), format("All the objects loaded are of type cat (%d)", all.size()));
    }

    @Test
    public void testTableForObject() throws Exception {
        say("Testing tableFor for object");
        List<?> all = orm().select(orm().tableFor(new Cat())).list();
        assertEquals(all.stream().filter(o -> o instanceof Cat).count(), all.size(), format("All the objects loaded are of type cat (%d)", all.size()));
    }
}
