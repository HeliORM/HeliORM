package com.heliorm.sql;


import com.heliorm.OrmException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import test.persons.Person;
import test.pets.Bird;
import test.pets.Cat;
import test.pets.CatBreed;
import test.pets.CatType;
import test.pets.Dog;
import test.pets.Pet;
import test.place.Province;
import test.place.Town;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.heliorm.Query.join;
import static com.heliorm.Query.on;
import static com.heliorm.Query.where;
import static com.heliorm.sql.TestData.makeBirds;
import static com.heliorm.sql.TestData.makeCatBreeds;
import static com.heliorm.sql.TestData.makeCats;
import static com.heliorm.sql.TestData.makeDogs;
import static com.heliorm.sql.TestData.makePersons;
import static com.heliorm.sql.TestData.makeProvinces;
import static com.heliorm.sql.TestData.makeTowns;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static test.Tables.CAT;
import static test.Tables.CATBREED;
import static test.Tables.PERSON;
import static test.Tables.PET;
import static test.Tables.PROVINCE;
import static test.Tables.TOWN;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SelectTest extends AbstractOrmTest {

    private static List<Province> provinces;
    private static List<Town> towns;
    private static List<Person> persons;
    private static List<CatBreed> catBreeds;
    private static List<Cat> cats;
    private static List<Dog> dogs;
    private static List<Bird> birds;

    @BeforeAll
    public static void setupData() throws OrmException {
        provinces = createAll(makeProvinces());
        towns = createAll(makeTowns(provinces));
        persons = createAll(makePersons(towns));
        catBreeds = createAll(makeCatBreeds());
        cats = createAll(makeCats(persons.size() * 5, persons, catBreeds));
        dogs = createAll(makeDogs(persons.size() * 4, persons));
        birds = createAll(makeBirds(persons.size() * 2, persons));
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

    private <O> void check(List<O> have, List<O> want) throws OrmException {
        assertNotNull(have, "The first list should be non-null");
        assertFalse(have.isEmpty(), "The first list should be non-empty");
        assertEquals(have.size(), want.size(), format("The two lists should be the same size (%d vs %s)", have.size(), want.size()));
        assertTrue(listCompareOrdered(have, want), "The items in the two lists should be exactly the same");
    }

    @Test
    @Order(10)
    public void testSelect() throws Exception {
        say("Testing simple select");
        List<Cat> all = orm().select(CAT).list();
        check(all, cats);
    }

    @Test
    @Order(20)
    public void testSelectWhereEq() throws Exception {
        say("Testing select with a simple where x = y clause");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() == 5)
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT, where(CAT.age.eq(5))).list();
        check(all, wanted);
    }

    @Test
    @Order(30)
    public void testSelectWhereNotEq() throws Exception {
        say("Testing select with a simple where x != y clause");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() != 5)
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT, where(CAT.age.notEq(5)))
                .list();
        check(all, wanted);
    }

    @Test
    @Order(40)
    public void testSelectWhereLt() throws Exception {
        say("Testing select with a simple where x < y clause");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() < 5)
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT, where(CAT.age.lt(5)))
                .list();
        check(all, wanted);
    }

    @Test
    @Order(50)
    public void testSelectWhereGt() throws Exception {
        say("Testing select with a simple where x > y clause");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() > 5)
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT, where(CAT.age.gt(5)))
                .list();
        check(all, wanted);
    }

    @Test
    @Order(60)
    public void testSelectWhereLe() throws Exception {
        say("Testing select with a simple where x <= y clause");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() <= 5)
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT, where(CAT.age.le(5)))
                .list();
        check(all, wanted);
    }

    @Test
    @Order(70)
    public void testSelectWhereGe() throws Exception {
        say("Testing select with a simple where x >= y clause");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() >= 5)
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT, where(CAT.age.ge(5)))
                .list();
        check(all, wanted);
    }

    @Test
    @Order(80)
    public void testSelectWhereIn() throws Exception {
        say("Testing select with a simple where x in (a,b,c) clause");
        List<Integer> ages = Arrays.asList(4, 5, 6);
        List<Cat> wanted = cats.stream()
                .filter(cat -> ages.contains(cat.getAge()))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT, where(CAT.age.in(ages)))
                .list();
        check(all, wanted);
    }

    @Test
    @Order(90)
    public void testSelectWhereNotIn() throws Exception {
        say("Testing select with a simple where x not in (a,b,c) clause");
        List<Integer> ages = Arrays.asList(4, 5, 6);
        List<Cat> wanted = cats.stream()
                .filter(cat -> !ages.contains(cat.getAge()))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT, where(CAT.age.notIn(ages)))
                .list();
        check(all, wanted);
    }

    @Test
    @Order(100)
    public void testSelectWhereLike() throws Exception {
        say("Testing select with a simple where x like a clause");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getName().startsWith("M"))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT, where(CAT.name.like("M%")))
                .list();
        check(all, wanted);
    }

    @Test
    @Order(110)
    public void testSelectWhereNotLike() throws Exception {
        say("Testing select with a simple where x not like a clause");
        List<Cat> wanted = cats.stream()
                .filter(cat -> !cat.getName().startsWith("M"))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT, where(CAT.name.notLike("M%")))
                .list();
        check(all, wanted);
    }

    @Test
    @Order(120)
    public void testSelectWhereOrWithOneField() throws Exception {
        say("Testing select with a simple where clause with or over one field");
        List<Cat> wanted = cats.stream()
                .filter(cat -> (cat.getAge() == 5) || (cat.getAge() == 10))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT,
                        where(CAT.age.eq(5)).or(CAT.age.eq(10)))
                .list();
        check(all, wanted);
    }

    @Test
    @Order(130)
    public void testSelectWhereAndWithRange() throws Exception {
        say("Testing select with a simple where clause with and over one field");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() > 5)
                .filter(cat -> cat.getAge() < 10)
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT,
                        where(CAT.age.gt(5))
                                .and(CAT.age.lt(10)))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertEquals(all.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    @Order(140)
    public void testSelectWhereAndWithTwoFields() throws Exception {
        say("Testing select with a simple where clause with and over two fields");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() < 5)
                .filter(cat -> cat.getType().equals(CatType.INDOOR))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT,
                        where(CAT.age.lt(5)).and(CAT.type.eq(CatType.INDOOR)))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertEquals(all.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    @Order(150)
    public void testSelectWhereOrWithOneFieldRange() throws Exception {
        say("Testing select with a where clause with or");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() < 5 || cat.getAge() > 10)
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT, where(CAT.age.lt(5)).or(CAT.age.gt(10)))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertEquals(all.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    @Order(160)
    public void testSelectWhereOrTwoFields() throws Exception {
        say("Testing select with a where clause with or on two fields");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() < 5 || cat.getType().equals(CatType.OUTDOOR))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT, where(CAT.age.lt(5)).or(CAT.type.eq(CatType.OUTDOOR)))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertEquals(all.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    @Order(161)
    public void testSelectWhereOrTwoFieldsInner() throws Exception {
        say("Testing select with a where clause with or on two fields");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() < 5 || cat.getType().equals(CatType.OUTDOOR))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT, where(CAT.age.lt(5)
                        .or(CAT.type.eq(CatType.OUTDOOR))))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertEquals(all.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }


    @Test
    @Order(162)
    public void testSelectWhereAndTwoFields() throws Exception {
        say("Testing select with a where clause with and on two fields");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() < 5 && cat.getType().equals(CatType.OUTDOOR))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT, where(CAT.age.lt(5)).and(CAT.type.eq(CatType.OUTDOOR)))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertEquals(all.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    @Order(163)
    public void testSelectWhereAndTwoFieldsInner() throws Exception {
        say("Testing select with a where clause with and on two fields");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() < 5 && cat.getType().equals(CatType.OUTDOOR))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT, where(CAT.age.lt(5).and(CAT.type.eq(CatType.OUTDOOR))))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertEquals(all.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    @Order(164)
    public void testSelectWhereNestedAndOr() throws Exception {
        say("Testing select with a where clause with nested and and or");
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getAge() < 5 || cat.getAge() > 8)
                .filter(cat -> cat.getType().equals(CatType.OUTDOOR))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT,
                        where(CAT.age.lt(5).or(CAT.age.gt(8)))
                                .and(CAT.type.eq(CatType.OUTDOOR)))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertEquals(all.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    @Order(165)
    public void testSelectWhereNestedMore() throws Exception {
        say("Testing select with a where clause with more nesting");
        List<Cat> some = cats.stream()
                .filter(cat -> cat.getAge() < 5 && cat.getType().equals(CatType.INDOOR))
                .collect(Collectors.toList());
        List<Cat> other = cats.stream()
                .filter(cat ->cat .getAge() > 8 &&  cat.getType().equals(CatType.OUTDOOR))
                .collect(Collectors.toList());
        List<Cat>wanted = new ArrayList<>();
        wanted.addAll(some);
        wanted.addAll(other);
        List<Cat> all = orm().select(CAT,
                        where(CAT.age.lt(5).and(CAT.type.eq(CatType.INDOOR)))
                                .or(CAT.age.gt(8).and(CAT.type.eq(CatType.OUTDOOR))))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertEquals(all.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }
    //    @Test
    @Order(167)
    public void testSelectWhereDateField() throws Exception {
        say("Testing select with a where clause with and on two fields");
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.YEAR, -5);
        Date then = cal.getTime();
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getBirthday().before(then))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT, where(CAT.birthday.lt(then)))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertEquals(all.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }


    //    @Test
    @Order(168)
    public void testSelectWhereAndTwoDateFields() throws Exception {
        say("Testing select with a where clause with and on two fields");
        Date today = new Date();
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.YEAR, -5);
        Date then = cal.getTime();
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getBirthday().after(then) && cat.getBirthday().before(today))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT, where(CAT.birthday.gt(then)).and(CAT.birthday.lt(today)))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertEquals(all.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    @Order(170)
    public void testSelectOrder() throws Exception {
        say("Testing select with a simple ordering");
        List<Town> wanted = towns.stream()
                .sorted(Comparator.comparing(Town::getName))
                .collect(Collectors.toList());
        List<Town> all = orm().select(TOWN)
                .orderBy(TOWN.name)
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertEquals(all.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareAsIs(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    @Order(180)
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
        assertEquals(all.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareAsIs(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    @Order(190)
    public void testSelectOrderThen() throws Exception {
        say("Testing select with a complex ordering");
        List<Cat> wanted = cats.stream()
                .sorted(Comparator.comparing(Cat::getAge)
                        .thenComparing(Cat::getName)
                        .thenComparing(Cat::getPersonId)
                        .thenComparing(Cat::getId))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT)
                .orderBy(CAT.age, CAT.name, CAT.personId, CAT.id)
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertEquals(all.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareAsIs(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    @Order(191)
    public void testSelectLimit() throws Exception {
        say("Testing select with a limit");
        int number = 10;
        List<Cat> all = orm().select(CAT)
                .limit(10)
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertEquals(all.size(), number, format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), number));
    }

    @Test
    @Order(192)
    public void testSelectOrderWithLimit() throws Exception {
        say("Testing select with a order and limit");
        List<Town> wanted = towns.stream()
                .sorted(Comparator.comparing(Town::getName))
                .limit(2)
                .collect(Collectors.toList());
        List<Town> all = orm().select(TOWN)
                .orderBy(TOWN.name)
                .limit(2)
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertEquals(all.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareAsIs(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    @Order(193)
    public void testLimitPaging() throws Exception {
        say("Testing select with a order and limit for paging");
        int batchSize = 10;
        AtomicInteger counter = new AtomicInteger();
        Map<Integer, List<Cat>> wanted = cats.stream()
                .sorted(Comparator.comparing(Cat::getId))
                .collect(Collectors.groupingBy(cat -> counter.getAndIncrement() / batchSize));
        Map<Integer, List<Cat>> all = new HashMap<>();
        for (int i = 0; i < counter.get() / batchSize; ++i) {
            List<Cat> batch = orm().select(CAT)
                    .orderBy(CAT.id)
                    .limit(i * batchSize, batchSize)
                    .list();
            all.put(i, batch);
        }
        assertNotNull(all, "The groups of cats should be non-null");
        for (int i = 0; i < counter.get() / batchSize; ++i) {
            List<Cat> wantedBatch = wanted.get(i);
            List<Cat> loadedBatch = all.get(i);
            assertEquals(loadedBatch.size(), wantedBatch.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
            assertTrue(listCompareAsIs(loadedBatch, wantedBatch), "The items loaded are exactly the same as the ones we expected");
        }
    }

    @Test
    @Order(200)
    public void testSelectOnAbstract() throws Exception {
        say("Testing select on an abstract object");
        List<Pet> all = orm().select(PET).list();
        List<Pet> wanted = new ArrayList<>();
        wanted.addAll(cats);
        wanted.addAll(dogs);
        wanted.addAll(birds);

        assertNotNull(all, "The list returned by list() should be non-null");
        assertEquals(all.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    @Order(202)
    public void testSelectOnAbstractWithWhere() throws Exception {
        say("Testing select on an abstract object with where");

        List<Pet> wanted = new ArrayList<>();
        wanted.addAll(cats.stream().filter(cat -> cat.getAge() < 6).collect(Collectors.toList()));
        wanted.addAll(dogs.stream().filter(cat -> cat.getAge() < 6).collect(Collectors.toList()));
        wanted.addAll(birds.stream().filter(cat -> cat.getAge() < 6).collect(Collectors.toList()));

        List<Pet> all = orm().select(PET, where(PET.age.lt(6))).list();

        assertNotNull(all, "The list returned by list() should be non-null");
        assertEquals(all.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    @Order(201)
    public void testSelectOnAbstractWithWhereAndOrder() throws Exception {
        say("Testing select on an abstract object with where and order");

        List<Pet> wanted = new ArrayList<>();
        wanted.addAll(cats.stream().filter(cat -> cat.getAge() < 6).collect(Collectors.toList()));
        wanted.addAll(dogs.stream().filter(cat -> cat.getAge() < 6).collect(Collectors.toList()));
        wanted.addAll(birds.stream().filter(cat -> cat.getAge() < 6).collect(Collectors.toList()));
        wanted = wanted.stream()
                .sorted(Comparator.comparing(Pet::getAge)
                        .thenComparing(Pet::getName)
                        .thenComparing(Pet::getPersonId))
                .collect(Collectors.toList());

        List<Pet> all = orm().select(PET,
                        where(PET.age.lt(6)))
                .orderBy(PET.age, PET.name, PET.personId)
                .list();

        assertNotNull(all, "The list returned by list() should be non-null");
        assertEquals(all.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareAsIs(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    @Order(210)
    public void testJoin() throws Exception {
        say("Testing select with a join");
        Person person = persons.get(0);
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getType() == CatType.INDOOR)
                .filter(cat -> cat.getPersonId().equals(person.getId()))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT,
                        where(CAT.type.eq(CatType.INDOOR)),
                        join(PERSON, on(CAT.personId, PERSON.id),
                                where(PERSON.emailAddress.eq(person.getEmailAddress()))))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertEquals(all.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    @Order(211)
    public void testJoinNoWhere() throws Exception {
        say("Testing select with a join without a where");
        Set<Long> personIds = persons.stream().map(Person::getId).collect(Collectors.toSet());
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getType() == CatType.INDOOR)
                .filter(cat -> personIds.contains(cat.getPersonId()))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT,
                        where(CAT.type.eq(CatType.INDOOR)),
                        join(PERSON, on(CAT.personId, PERSON.id)))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertEquals(all.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    @Order(220)
    public void testJoinThenJoin() throws Exception {
        say("Testing select with a join and then join");
        Optional<Town> opt = towns.stream()
                .filter(t -> t.getName().equals("Durban"))
                .findFirst();
        if (!opt.isPresent()) {
            throw new NoSuchElementException("No town found in test");
        }
        Town town = opt.get();
        List<Long> fromThere = persons.stream()
                .filter(p -> p.getTownId().equals(town.getId()))
                .filter(p -> p.getIncome() > 5000)
                .map(Person::getId)
                .collect(Collectors.toList());
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getType() == CatType.INDOOR)
                .filter(cat -> fromThere.contains(cat.getPersonId()))
                .collect(Collectors.toList());
        List<Cat> all = orm().select(CAT,
                        where(CAT.type.eq(CatType.INDOOR)),
                        join(PERSON, on(CAT.personId, PERSON.id),
                                where(PERSON.income.gt(5000)), join(TOWN, on(PERSON.townId, TOWN.id),
                                        where(TOWN.name.eq("Durban")))))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertEquals(all.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");

    }

    @Test
    @Order(230)
    public void testJoinThenJoinThenJoin() throws Exception {
        say("Testing select with a join and then join and then join");
        Province province = provinces.stream()
                .filter(p -> p.getName().equals("Western Cape"))
                .findFirst().get();
        List<Long> towns = SelectTest.towns.stream()
                .filter(t -> t.getProvinceId().equals(province.getProvinceId()))
                .map(Town::getId)
                .collect(Collectors.toList());
        List<Long> fromThere = persons.stream()
                .filter(p -> towns.contains(p.getTownId()))
                .filter(p -> p.getIncome() > 5000)
                .map(Person::getId)
                .collect(Collectors.toList());
        List<Cat> wanted = cats.stream()
                .filter(cat -> cat.getType() == CatType.INDOOR)
                .filter(cat -> fromThere.contains(cat.getPersonId()))
                .collect(Collectors.toList());

        List<Cat> all = orm().select(CAT,
                        where(CAT.type.eq(CatType.INDOOR)),
                        join(PERSON, on(CAT.personId, PERSON.id),
                                where(PERSON.income.gt(5000)),
                                join(TOWN, on(PERSON.townId, TOWN.id),
                                        join(PROVINCE, on(TOWN.provinceId, PROVINCE.provinceId),
                                                where(PROVINCE.name.eq("Western Cape"))))))
                .list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertEquals(all.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", all.size(), wanted.size()));
        assertTrue(listCompareOrdered(all, wanted), "The items loaded are exactly the same as the ones we expected");

    }

    @Test
    @Order(240)
    public void testSelectJoinWithSameKeys() throws Exception {
        say("Testing select with a join with same key names");
        List<Town> selected = orm().select(TOWN,
                        join(PROVINCE, on(TOWN.provinceId, PROVINCE.provinceId),
                                where(PROVINCE.name.eq(provinces.get(0).getName()))))
                .list();

        List<Town> wanted = towns.stream()
                .filter(town -> town.getProvinceId().equals(provinces.get(0).getProvinceId()))
                .collect(Collectors.toList());

        assertNotNull(selected, "The list returned by list() should be non-null");
        assertEquals(selected.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", selected.size(), wanted.size()));
        assertTrue(listCompareOrdered(selected, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    @Order(250)
    public void testSelectAbstractJoinWithSameKeys() throws Exception {
        say("Testing select of abstract type with a join with same key names");
        List<Pet> selected = orm().select(PET,
                        join(PERSON, on(PET.personId, PERSON.id),
                                where(PERSON.firstName.eq(persons.get(0).getFirstName()))))
                .list();

        Map<Long, Person> personMap = persons.stream()
                .filter(person -> person.getFirstName().equals(persons.get(0).getFirstName()))
                .collect(Collectors.toMap(Person::getId, person -> person));

        List<Pet> wanted = Stream.concat(Stream.concat(cats.stream(), dogs.stream()), birds.stream())
                .filter(pet -> personMap.containsKey(pet.getPersonId()))
                .collect(Collectors.toList());

        assertNotNull(selected, "The list returned by list() should be non-null");
        assertEquals(selected.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", selected.size(), wanted.size()));
        assertTrue(listCompareOrdered(selected, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    @Order(251)
    public void joinOnTwoTables() throws Exception {
        say("Testing select with join against two tables");
        List<Cat> selected = orm().select(CAT,
                        join(PERSON, on(CAT.personId, PERSON.id), where(PERSON.firstName.eq("Bob"))),
                        join(CATBREED, on(CAT.breedId, CATBREED.id),
                                where(CATBREED.name.eq("Persian"))))
                .list();

        Map<Long, Person> personMap = persons.stream()
                .filter(person -> person.getFirstName().equals("Bob"))
                .collect(Collectors.toMap(Person::getId, person -> person));
        CatBreed persian = catBreeds.stream().filter(breed -> breed.getName().equals("Persian")).findFirst().get();

        List<Cat> wanted = cats.stream().filter(cat -> cat.getBreedId().equals(persian.getId()))
                .filter(cat -> personMap.containsKey(cat.getPersonId()))
                .collect(Collectors.toList());

        assertNotNull(selected, "The list returned by list() should be non-null");
        assertEquals(selected.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", selected.size(), wanted.size()));
        assertTrue(listCompareOrdered(selected, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    @Order(260)
    public void testSelectIsNull() throws Exception {
        say("Testing select of data on is null");
        List<Person> selected = orm().select(PERSON,
                        where(PERSON.lastName.isNull()))
                .list();
        List<Person> wanted = persons.stream()
                .filter(person -> person.getLastName() == null)
                .collect(Collectors.toList());
        assertNotNull(selected, "The list returned by list() should be non-null");
        assertEquals(selected.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", selected.size(), wanted.size()));
        assertNotNull(selected, "The list returned by list() should be non-null");
        assertTrue(listCompareOrdered(selected, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    @Order(270)
    public void testSelectIsNotNull() throws Exception {
        say("Testing select of data on is not null");
        List<Person> selected = orm().select(PERSON,
                        where(PERSON.lastName.isNotNull()))
                .list();
        List<Person> wanted = persons.stream()
                .filter(person -> person.getLastName() != null)
                .collect(Collectors.toList());
        assertNotNull(selected, "The list returned by list() should be non-null");
        assertEquals(selected.size(), wanted.size(), format("The amount of loaded data should match the number of the items expected (%d vs %s)", selected.size(), wanted.size()));
        assertNotNull(selected, "The list returned by list() should be non-null");
        assertTrue(listCompareOrdered(selected, wanted), "The items loaded are exactly the same as the ones we expected");
    }

    @Test
    @Order(280)
    public void testSelectWhereInEmpty() {
        say("Testing select with a simple where x in empty list - must fail");
        assertThrows(OrmException.class, () -> {
            List<Integer> ages = Collections.emptyList();
            orm().select(CAT, where(CAT.age.in(ages)))
                    .list();
        });
    }

    @Test
    @Order(290)
    public void testSelectWhereNotInEmpty() {
        say("Testing select with a simple where x not in empty list - must fail");
        assertThrows(OrmException.class, () -> {
            List<Integer> ages = Collections.emptyList();
            orm().select(CAT, where(CAT.age.notIn(ages)))
                    .list();

        });
    }
}
