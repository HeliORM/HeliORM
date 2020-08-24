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

import static com.heliorm.TestData.*;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static test.Tables.*;

public class SelectTest extends AbstractOrmTest {

    private static final int MAX_CATS = 20;
    private static final int MAX_DOGS = 15;
    private static final int MAX_PERSONS = 3;
    private static List<Person> persons;
    private static List<Cat> cats;
    private static List<Dog> dogs;

    @BeforeAll
    public static void setupData() throws OrmException {
        persons = createAll(makePersons(MAX_PERSONS));
        cats = createAll(makeCats(MAX_CATS, persons));
        dogs = createAll(makeDogs(MAX_DOGS, persons));
    }


    @Test
    public void testSelect() throws Exception {
        List<Cat> all = orm.select(CAT).list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertFalse(all.isEmpty(), "The list returned by list() should be non-empty");
        assertTrue(all.size() == MAX_CATS, format("The amount of loaded data should match the number of the items (%d vs %s)", all.size(), MAX_CATS));
        assertTrue(listCompareOrdered(all, cats), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    public void testSelectWhere() throws Exception {
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() < 5)
                .collect(Collectors.toList());
        List<Cat> all = orm.select(CAT)
                .where(CAT.age.lt(5))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertTrue(all.size() == wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }


    @Test
    public void testSelectWhereAnd() throws Exception {
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() < 5)
                .filter(cat -> cat.getType().equals(Cat.Type.INDOOR))
                .collect(Collectors.toList());
        List<Cat> all = orm.select(CAT)
                .where(CAT.age.lt(5)).and(CAT.type.eq(Cat.Type.INDOOR))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertTrue(all.size() == wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    public void testSelectWhereOr() throws Exception {
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() < 5 ||  cat.getAge() > 10)
                .collect(Collectors.toList());
        List<Cat> all = orm.select(CAT)
                .where(CAT.age.lt(5)).or(CAT.age.gt(10))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertTrue(all.size() == wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    public void testSelectOrder() throws Exception {
        List<Cat> wanted = cats.stream()
                .sorted(Comparator.comparing(Cat::getName))
                .collect(Collectors.toList());
        List<Cat> all = orm.select(CAT)
                .orderBy(CAT.name)
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertTrue(all.size() == wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareAsIs(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    public void testSelectOrderDesc() throws Exception {
        List<Cat> wanted = cats.stream()
                .sorted((c1,c2) -> c2.getName().compareTo(c1.getName()))
                .collect(Collectors.toList());
        List<Cat> all = orm.select(CAT)
                .orderByDesc(CAT.name)
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertTrue(all.size() == wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareAsIs(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }


    @Test
    public void testSelectOrderThen() throws Exception {
        List<Cat> wanted = cats.stream()
                .sorted((c1,c2) -> {
                    int o = c1.getAge() - c2.getAge();
                    if (o == 0) {
                        return c1.getName().compareTo(c2.getName());
                    }
                    return o;
                })
                .collect(Collectors.toList());
        List<Cat> all = orm.select(CAT)
                .orderBy(CAT.age)
                .thenBy(CAT.name)
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertTrue(all.size() == wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareAsIs(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    public void testSelectOnAbstract() throws Exception {
        List< Mamal> all = orm.select(MAMAL).list();
        List< Mamal> wanted = new ArrayList<>();
        wanted.addAll(cats);
        wanted.addAll(dogs);
        assertNotNull(all, "The list returned by list() should be non-null");
        assertTrue(all.size() == wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    public void testJoin() throws Exception {
        Person person = persons.get(0);
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getPersonId() == person.getId())
                .collect(Collectors.toList());
        List<Cat> all = orm.select(CAT)
                .join(PERSON).on(CAT.personId,PERSON.id)
                .where(PERSON.emailAddress.eq(person.getEmailAddress()))
                .list();
        List<Cat> forPerson = all.stream()
                .filter(cat -> cat.getPersonId() == person.getId())
                .collect(Collectors.toList());
        assertNotNull(all, "The list returned by list() should be non-null");
        assertTrue(all.size() == wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
        assertTrue(listCompareOrdered(forPerson, all), "The items loaded are exactly the same as the ones we expected");
    }

}
