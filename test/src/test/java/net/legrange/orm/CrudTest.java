package net.legrange.orm;

import net.legrange.orm.pets.Cat;
import org.junit.jupiter.api.Test;

import static net.legrange.orm.TestData.makeCat;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CrudTest  extends AbstractOrmTest {


    @Test
    public void testCreate() throws Exception {
        Cat cat = makeCat();
        Cat saved = orm.create(cat);
        assertTrue(pojoCompare(cat, saved));
    }

}
