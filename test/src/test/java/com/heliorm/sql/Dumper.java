package com.heliorm.sql;

import com.heliorm.OrmException;
import com.heliorm.def.Continuation;
import com.heliorm.impl.Part;
import org.junit.jupiter.api.Test;
import test.Tables;
import test.pets.Cat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static test.Tables.CAT;

public class Dumper extends AbstractOrmTest {

    @Test
    public void dumpBrokenQuery() throws OrmException {
        Continuation<Tables.CatTable, Cat, Tables.CatTable, Cat> query = orm().select(CAT)
                .where(CAT.age.gt(5))
                .and(CAT.age.lt(10));
        dump(0, "root", ((Part) query).head());
    }

    private Map<Part, String> visit = new HashMap<>();

    private void dump(int depth, String side, Part part) {
        indent(depth);
        System.out.printf("%s: ", side);
        if (part != null) {
            if (visit.containsKey(part)) {
                System.out.printf("%s\n", visit.get(part));
            }
            else {
                String id = UUID.randomUUID().toString();
                visit.put(part, id);
                visit.put(part, id);
                System.out.printf("%s: (%s)\n", part, id);
                dump(depth + 1,"left", part.left());
                dump(depth + 1, "right", part.right());
            }
        }
        else {
            System.out.printf("null\n");
        }
    }

    private void indent(int depth){
        for (int i = 0; i < depth; ++i ) {
            System.out.printf("  ");
        }
    }
}
