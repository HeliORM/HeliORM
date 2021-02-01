package com.heliorm.sql;

import com.heliorm.OrmException;
import com.heliorm.def.Continuation;
import com.heliorm.impl.Part;
import org.junit.jupiter.api.Test;
import test.Tables;
import test.pets.Cat;

import java.util.HashMap;
import java.util.Map;

import static test.Tables.CAT;

public class Dumper extends AbstractOrmTest {

    @Test
    public void dumpBrokenQuery() throws OrmException {
        Continuation<Tables.CatTable, Cat, Tables.CatTable, Cat> query = orm().select(CAT)
                .where(CAT.age.gt(5))
                .and(CAT.age.lt(10));
        dump(((Part) query).head());
    }

    private Map<Part, String> visit = new HashMap<>();

    private void dump(Part part) {
        System.out.printf("%s ", part);
        if (part.right() != null) {
            dump(part.right());
        }
    }

    private void indent(int depth){
        for (int i = 0; i < depth; ++i ) {
            System.out.printf("  ");
        }
    }
}
