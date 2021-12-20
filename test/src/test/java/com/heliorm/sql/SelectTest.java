package com.heliorm.sql;


import com.heliorm.OrmException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import test.persons.Person;
import test.pets.*;
import test.place.Province;
import test.place.Town;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.heliorm.sql.TestData.*;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static test.Tables.*;

public class SelectTest extends AbstractOrmTest {

    private static List<Province> provinces;
    private static List<Town> towns;
    private static List<Person> persons;
    private static List<Cat> cats;
    private static List<Dog> dogs;
    private static List<Bird> birds;

    private <O> void check(List<O> have, List<O> want) throws OrmException {
        assertNotNull(have, "The first list should be non-null");
        assertFalse(have.isEmpty(), "The first list should be non-empty");
        assertTrue(have.size() == want.size(), format("The two lists should be the same size (%d vs %s)", have.size(), want.size()));
        assertTrue(listCompareOrdered(have, want), "The items in the two lists should be exactly the same");
    }

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
        check(all, cats);
    }

    @Test
    public void testSelectWhereEq() throws Exception {
        say("Testing select with a simple where x = y clause");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() == 5)
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT)
                .where(CAT.age.eq(5))
                .list();
        check(all, wanted);
    }

    @Test
    public void testSelectWhereNotEq() throws Exception {
        say("Testing select with a simple where x != y clause");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() != 5)
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT)
                .where(CAT.age.notEq(5))
                .list();
        check(all, wanted);
    }

    @Test
    public void testSelectWhereLt() throws Exception {
        say("Testing select with a simple where x < y clause");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() < 5)
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT)
                .where(CAT.age.lt(5))
                .list();
        check(all, wanted);
    }

    @Test
    public void testSelectWhereGt() throws Exception {
        say("Testing select with a simple where x > y clause");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() > 5)
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT)
                .where(CAT.age.gt(5))
                .list();
        check(all, wanted);
    }

    @Test
    public void testSelectWhereLe() throws Exception {
        say("Testing select with a simple where x <= y clause");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() <=5)
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT)
                .where(CAT.age.le(5))
                .list();
        check(all, wanted);
    }

    @Test
    public void testSelectWhereGe() throws Exception {
        say("Testing select with a simple where x >= y clause");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() >= 5)
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT)
                .where(CAT.age.ge(5))
                .list();
        check(all, wanted);
    }

    @Test
    public void testSelectWhereIn() throws Exception {
        say("Testing select with a simple where x in (a,b,c) clause");
        List<Integer> ages = Arrays.asList(4,5,6);
        List<Cat> wanted = cats.stream()
                .filter(cat -> ages.contains(cat.getAge()))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT)
                .where(CAT.age.in(ages))
                .list();
        check(all, wanted);
    }

    @Test
    public void testSelectWhereNotIn() throws Exception {
        say("Testing select with a simple where x not in (a,b,c) clause");
        List<Integer> ages = Arrays.asList(4,5,6);
        List<Cat> wanted = cats.stream()
                .filter(cat -> !ages.contains(cat.getAge()))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT)
                .where(CAT.age.notIn(ages))
                .list();
        check(all, wanted);
    }

    @Test
    public void testSelectWhereLike() throws Exception {
        say("Testing select with a simple where x like a clause");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getName().startsWith("M"))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT)
                .where(CAT.name.like("M%"))
                .list();
        check(all, wanted);
    }

    @Test
    public void testSelectWhereNotLike() throws Exception {
        say("Testing select with a simple where x not like a clause");
        List<Cat> wanted = cats.stream()
                .filter(cat -> !cat.getName().startsWith("M"))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT)
                .where(CAT.name.notLike("M%"))
                .list();
        check(all, wanted);
    }

    @Test
    public void testSelectWhereOrWithOneField() throws Exception {
        say("Testing select with a simple where clause with and over one field");
        List<Cat> wanted = cats.stream()
                .filter(cat -> (cat.getAge() == 5) || (cat.getAge() == 10))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT)
                .where(CAT.age.eq(5))
                .or(CAT.age.eq(10))
                .list();
        check(all, wanted);
    }

    @Test
    public void testSelectWhereAndWithRange() throws Exception {
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
    public void testSelectWhereOrWithOneFieldRange() throws Exception {
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
                .orderBy(CAT.name.desc(), CAT.age.desc(), CAT.type.desc(), CAT.personId.desc())
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
                .orderBy(CAT.age, CAT.name, CAT.personId.asc(), CAT.id)
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
                .filter(cat -> cat.getType() == CatType.INDOOR)
                .filter(cat -> cat.getPersonId() == person.getId())
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT)
                .where(CAT.type.eq(CatType.INDOOR))
                .join(PERSON).on(CAT.personId, PERSON.id)
                .where(PERSON.emailAddress.eq(person.getEmailAddress()))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertTrue(all.size() == wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    public void testJoinThenJoin() throws Exception {
        say("Testing select with a join and then join");
        Town town = towns.stream()
                .filter(t -> t.getName().equals("Durban"))
                .findFirst().get();
        List<Long> fromThere = persons.stream()
                .filter(p -> p.getTownId() == town.getId())
                .map(p -> p.getId())
                .collect(Collectors.toList());
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getType() == CatType.INDOOR)
                .filter(cat -> fromThere.contains(cat.getPersonId()))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT)
                .where(CAT.type.eq(CatType.INDOOR))
                .join(PERSON).on(CAT.personId, PERSON.id)
                .thenJoin(TOWN).on(PERSON.townId, TOWN.id)
                .where(TOWN.name.eq("Durban"))
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

    @Test
    public void testSelectWhereInEmpty() throws Exception {
        say("Testing select with a simple where x in empty list - must fail");
        assertThrows(OrmException.class, () -> {
            List<Integer> ages = Arrays.asList();
            List<Cat> all = orm().select(CAT)
                    .where(CAT.age.in(ages))
                    .list();
        });
    }

    @Test
    public void testSelectWhereNotInEmpty() throws Exception {
        say("Testing select with a simple where x not in empty list - must fail");
        assertThrows(OrmException.class, () -> {
            List<Integer> ages = Arrays.asList();
            List<Cat> all = orm().select(CAT)
                    .where(CAT.age.notIn(ages))
                    .list();

        });
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
