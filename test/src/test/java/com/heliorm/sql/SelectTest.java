package com.heliorm.sql;


import com.heliorm.OrmException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import test.persons.Person;
import test.pets.Bird;
import test.pets.Cat;
import test.pets.CatType;
import test.pets.Dog;
import test.pets.Pet;
import test.place.Province;
import test.place.Town;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.heliorm.sql.TestData.makeBirds;
import static com.heliorm.sql.TestData.makeCats;
import static com.heliorm.sql.TestData.makeDogs;
import static com.heliorm.sql.TestData.makePersons;
import static com.heliorm.sql.TestData.makeProvinces;
import static com.heliorm.sql.TestData.makeTowns;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static test.Tables.CAT;
import static test.Tables.PERSON;
import static test.Tables.PET;
import static test.Tables.PROVINCE;
import static test.Tables.TOWN;

public class SelectTest extends AbstractOrmTest {

    private static List<Province> provinces;
    private static List<Town> towns;
    private static List<Person> persons;
    private static List<Cat> cats;
    private static List<Dog> dogs;
    private static List<Bird> birds;


    @BeforeAll
    public static void setupData() throws OrmException {
        provinces = createAll(makeProvinces());
        towns = createAll(makeTowns(provinces));
        persons = createAll(makePersons(towns));
        cats = createAll(makeCats(persons.size() * 5, persons));
        dogs = createAll(makeDogs(persons.size() * 4, persons));
        birds = createAll(makeBirds(persons.size()*2, persons));
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
    public void testSelectWhereAndWithOneField() throws Exception {
        say("Testing select with a simple where clause with and over one field");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() > 5)
                .filter(cat -> cat.getAge() < 10)
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT)
                .where(CAT.age.gt(5))
                .and(CAT.age.lt(10))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertTrue(all.size() == wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    public void testSelectWhereAndWithTwoFields() throws Exception {
        say("Testing select with a simple where clause with and over two fields");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() < 5)
                .filter(cat -> cat.getType().equals(CatType.INDOOR))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT)
                .where(CAT.age.lt(5)).and(CAT.type.eq(CatType.INDOOR))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertTrue(all.size() == wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    public void testSelectWhereOrWithOneField() throws Exception {
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
    public void testSelectWhereOrTwoFields() throws Exception {
        say("Testing select with a where clause with or on two fields");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() < 5 || cat.getType().equals(CatType.OUTDOOR))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT)
                .where(CAT.age.lt(5)).or(CAT.type.eq(CatType.OUTDOOR))
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
        List<Pet> all = orm().select(PET).list();
        List<Pet> wanted = new ArrayList<>();
        wanted.addAll(cats);
        wanted.addAll(dogs);
        wanted.addAll(birds);
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
    public void testSelectJoinWithSameKeys() throws Exception {
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

    @Test
    public void testSelectAbstractJoinWithSameKeys() throws Exception {
        say("Testing select of abstract type with a join with same key names");
        List<Pet> selected = orm().select(PET)
                .join(PERSON).on(PET.personId, PERSON.id)
                .where(PERSON.firstName.eq(persons.get(0).getFirstName()))
                .list();

        Map<Long, Person> personMap = persons.stream()
                .filter(person -> person.getFirstName().equals(persons.get(0).getFirstName()))
                .collect(Collectors.toMap(person -> person.getId(), person -> person));

        List<Pet> wanted = Stream.concat(Stream.concat(cats.stream(), dogs.stream()), birds.stream())
                .filter(pet -> personMap.containsKey(pet.getPersonId()))
                .collect(Collectors.toList());

        assertNotNull(selected, "The list returned by list() should be non-null");
        assertTrue(selected.size() == wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", selected.size(), wanted.size()));
        assertTrue(listCompareOrdered(selected, wanted), "The items loaded are exactly the same as the ones we expected");
    }


    @Test
    public void testSelectIsNull() throws Exception {
        say("Testing select of data on is null");
        List<Person> selected = orm().select(PERSON)
                .where(PERSON.lastName.isNull())
                .list();
        List<Person> wanted = persons.stream()
                .filter(person ->  person.getLastName() == null)
                .collect(Collectors.toList());
        assertNotNull(selected, "The list returned by list() should be non-null");
        assertTrue(selected.size() == wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", selected.size(), wanted.size()));
        assertNotNull(selected, "The list returned by list() should be non-null");
        assertTrue(listCompareOrdered(selected, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    public void testSelectIsNotNull() throws Exception {
        say("Testing select of data on is not null");
        List<Person> selected = orm().select(PERSON)
                .where(PERSON.lastName.isNotNull())
                .list();
        List<Person> wanted = persons.stream()
                .filter(person ->  person.getLastName() != null)
                .collect(Collectors.toList());
        assertNotNull(selected, "The list returned by list() should be non-null");
        assertTrue(selected.size() == wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", selected.size(), wanted.size()));
        assertNotNull(selected, "The list returned by list() should be non-null");
        assertTrue(listCompareOrdered(selected, wanted), "The items loaded are exactly the same as the ones we expected");
    }


    @AfterAll
    public static void removeData() throws OrmException {
        deleteAll(Cat.class);
        deleteAll(Dog.class);
        deleteAll(Bird.class);
        deleteAll(Person.class);
        deleteAll(Town.class);
        deleteAll(Province.class);
    }
}
