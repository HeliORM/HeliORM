package com.heliorm;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import test.pets.Cat;
import test.pets.Dog;
import test.pets.Mamal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.heliorm.TestData.makeCats;
import static com.heliorm.TestData.makeDogs;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static test.Tables.CAT;
import static java.lang.String.format;
import static test.Tables.MAMAL;

public class SelectTest extends AbstractOrmTest {

    private static final int MAX_CATS = 20;
    private static final int MAX_DOGS = 15;
    private static List<Cat> cats;
    private static List<Dog> dogs;

    @BeforeAll
    public static void setupData() throws OrmException {
        cats = createAll(makeCats(MAX_CATS));
        dogs = createAll(makeDogs(MAX_DOGS));
    }


    @Test
    public void testSelect() throws Exception {
        List<Cat> all = orm.select(CAT).list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertFalse(all.isEmpty(), "The list returned by list() should be non-empty");
        assertTrue(all.size() == MAX_CATS, format("The amount of loaded data should match the number of the items (%d vs %s)", all.size(), MAX_CATS));
        assertTrue(listCompareAsIs(all, cats), "The items loaded are exactly the same as the ones we expected");
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
        assertTrue(listCompareAsIs(all, wanted), "The items loaded are exactly the same as the ones we expected");
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

}
