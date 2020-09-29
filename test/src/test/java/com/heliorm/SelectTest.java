package com.heliorm;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import test.persons.Person;
import test.pets.Cat;
import test.pets.Dog;
import test.pets.Mamal;
import test.place.Province;
import test.place.Town;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.heliorm.TestData.*;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static test.Tables.*;

public class SelectTest extends AbstractOrmTest {

    private static List<Province> provinces;
    private static List<Town> towns;
    private static List<Person> persons;
    private static List<Cat> cats;
    private static List<Dog> dogs;


    @BeforeAll
    public static void setupData() throws OrmException {
        provinces = createAll(makeProvinces());
        towns = createAll(makeTowns(provinces));
        persons = createAll(makePersons(towns));
        cats = createAll(makeCats(persons.size() * 5, persons));
        dogs = createAll(makeDogs(persons.size() * 4, persons));
    }


    @Test
    public void testSelect() throws Exception {
        say("Testing simple select");
        List<Cat> all = orm().select(CAT).list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertFalse(all.isEmpty(), "The list returned by list() should be non-empty");
        assertTrue(all.size() == cats.size(), format("The amount of loaded data should match the number of the items (%d vs %s)", all.size(), cats.size()));
        assertTrue(listCompareOrdered(all, cats), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    public void testSelectWhere() throws Exception {
        say("Testing select with a simple where clause");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() < 5)
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT)
                .where(CAT.age.lt(5))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertTrue(all.size() == wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }


    @Test
    public void testSelectWhereAnd() throws Exception {
        say("Testing select with a simple where clause with and");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() < 5)
                .filter(cat -> cat.getType().equals(Cat.Type.INDOOR))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT)
                .where(CAT.age.lt(5)).and(CAT.type.eq(Cat.Type.INDOOR))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertTrue(all.size() == wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    public void testSelectWhereOr() throws Exception {
        say("Testing select with a where clause with or");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() < 5 || cat.getAge() > 10)
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT)
                .where(CAT.age.lt(5)).or(CAT.age.gt(10))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertTrue(all.size() == wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    public void testSelectOrder() throws Exception {
        say("Testing select with a simple ordering");
        List<Town> wanted = towns.stream()
                .sorted(Comparator.comparing(Town::getName))
                .collect(Collectors.toList());
        List<Town> all = orm().select(TOWN)
                .orderBy(TOWN.name)
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertTrue(all.size() == wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareAsIs(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    public void testSelectOrderDesc() throws Exception {
        say("Testing select with a descending ordering");
        List<Cat> wanted = cats.stream()
                .sorted(Comparator.comparing(Cat::getName)
                        .thenComparing(Cat::getAge)
                        .thenComparing(Cat::getType)
                        .thenComparing(Cat::getPersonId)
                        .reversed())
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT)
                .orderByDesc(CAT.name)
                .thenByDesc(CAT.age)
                .thenByDesc(CAT.type)
                .thenByDesc(CAT.personId)
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertTrue(all.size() == wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareAsIs(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }


    @Test
    public void testSelectOrderThen() throws Exception {
        say("Testing select with a complex ordering");
        List<Cat> wanted = cats.stream()
                .sorted(Comparator.comparing(Cat::getAge)
                        .thenComparing(Cat::getName)
                        .thenComparing(Cat::getPersonId)
                        .thenComparing(Cat::getId))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT)
                .orderBy(CAT.age)
                .thenBy(CAT.name)
                .thenBy(CAT.personId)
                .thenBy(CAT.id)
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertTrue(all.size() == wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareAsIs(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    public void testSelectOnAbstract() throws Exception {
        say("Testing select on an abstract object");
        List<Mamal> all = orm().select(MAMAL).list();
        List<Mamal> wanted = new ArrayList<>();
        wanted.addAll(cats);
        wanted.addAll(dogs);
        assertNotNull(all, "The list returned by list() should be non-null");
        assertTrue(all.size() == wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    public void testJoin() throws Exception {
        say("Testing select with a join");
        Person person = persons.get(0);
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getPersonId() == person.getId())
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT)
                .join(PERSON).on(CAT.personId, PERSON.id)
                .where(PERSON.emailAddress.eq(person.getEmailAddress()))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertTrue(all.size() == wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    public void testJoinWithSameKeys() throws Exception {
        say("Testing select with a join with same key names");
        List<Town> selected = orm().select(TOWN)
                .join(PROVINCE).on(TOWN.provinceId, PROVINCE.provinceId)
                .where(PROVINCE.name.eq(provinces.get(0).getName()))
                .list();

        List<Town> wanted = towns.stream()
                .filter(town -> town.getProvinceId().equals(provinces.get(0).getProvinceId()))
                .collect(Collectors.toList());

        assertNotNull(selected, "The list returned by list() should be non-null");
        assertTrue(selected.size() == wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", selected.size(), wanted.size()));
        assertTrue(listCompareOrdered(selected, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @AfterAll
    public static void removeData() throws OrmException {
        deleteAll(Cat.class);
        deleteAll(Dog.class);
        deleteAll(Person.class);
        deleteAll(Town.class);
        deleteAll(Province.class);
    }
}
