package net.legrange.orm;

import net.legrange.orm.pets.Cat;
import org.junit.jupiter.api.Test;

import static net.legrange.orm.TestData.makeCat;
import static net.legrange.orm.TestOrm.orm;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class CrudTests {

    @Test
    public void testCreate() throws OrmException {
        Cat cat = makeCat();
        Cat saved = orm().create(cat);
        assertEquals(cat.getName(), saved.getName());
    }

}
