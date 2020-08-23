package net.legrange.orm;

import net.legrange.orm.pets.Cat;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static net.legrange.orm.Tables.CAT;
import static net.legrange.orm.TestData.makeCat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CrudTest  extends AbstractOrmTest {

    private List<Cat> cats = new ArrayList<>();

    @Test
    public void testCreate() throws Exception {
        Cat cat = makeCat();
        Cat saved = orm.create(cat);
        Cat loaded = orm.select(CAT).where(CAT.id.eq(saved.getId())).one();

        assertNotNull(saved.getId(), "The POJO returned by create() should be non-null");
        assertTrue(cat.getClass().equals(saved.getClass()), "The POJO returned by create() must be the same type as the input POJO");
        assertNotNull(saved.getId(), "The auto-number key should be non-null after create");
        assertTrue(pojoCompareExcludingKey(cat, saved),"Fields (excluding the primary key) should be the same after save");
        assertNotEquals(cat.getId(), saved.getId(), "The primary key on an auto-number field should change on create");
        assertTrue(pojoCompare(saved, loaded), "After create, the object should be loadable on it's primary key");
    }

    @Test
    public void testSelectAll() throws Exception {
        Cat cat = makeCat();
        Cat saved = orm.create(cat);
        List<Cat> all = orm.select(CAT).list();
        assertNotNull(all, "The list returned by list() should be non-null");
        assertFalse(all.isEmpty(), "The list returned by select must be non-empty (in this test)");
    }




}
