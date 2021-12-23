package com.heliorm.sql;

import com.heliorm.Table;
import com.heliorm.impl.ExecutablePart;
import com.heliorm.impl.JoinPart;
import com.heliorm.impl.OrderPart;
import com.heliorm.impl.Part;
import com.heliorm.impl.SelectPart;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class AbstractionHelper {

    /** Expand the query parts in the list so that the query branches into
     * multiple table queries when a select or join with a table which has
     * sub-tables is encountered.
     * @param tail part
     * @return The expanded query parts lists.
     */
     List<SelectPart<?,?>> explodeAbstractions(ExecutablePart<?,?> tail) {
         return explode( tail.getSelect());
    }

    /**
     * Create a comparator based on the tail of the query that will compare
     * Pojos on their fields.
     *
     * @param <O>
     * @return
     */
     <T extends Table<O>, O> Comparator<PojoCompare<O>> makeComparatorForTail(List<OrderPart<T,O>> order) {
        List<Comparator<PojoCompare<O>>> comps = new LinkedList();
        for (OrderPart op : order) {
            comps.add((PojoCompare<O> w1, PojoCompare<O> w2) -> {
                if (op.getDirection() == OrderPart.Direction.ASCENDING) {
                    return w1.compareTo(w2, op.getField());
                } else {
                    return w2.compareTo(w1, op.getField());

                }
            });
        }
        return new CompoundComparator(comps);
    }

    /**
     * Convert the supplied part chain to a list of sequenctial parts. This is
     * required to pass the list of parts to the parser so it can generate a
     * query strucutre.
     *
     * @param tail The tail part
     * @return The list of parts
     */
     private List<Part> tailToList(Part tail) {
        List<Part> parts = new ArrayList();
        Part part = tail.head();
        while (part != null) {
            parts.add(part);
            part = part.right();
        }
        return parts;
    }

    private List<SelectPart<?,?>> explode(SelectPart<?,?> select) {
        List<SelectPart<?, ?>> res = new ArrayList<>();
        Table<?> table = select.getTable();
        Set<Table<?>> subTables = table.getSubTables();
        if (subTables.isEmpty()) {
            res.add(new SelectPart(select.getSelector(), select.getTable(), select.getWhere(), explode(select.getJoins())));
        } else {
            for (Table<?> subTable : subTables) {
                res.add(new SelectPart(select.getSelector(), subTable, select.getWhere(), explode(select.getJoins())));
            }
        }
        return res;
    }

    private List<JoinPart<?,?,?,?>> explode(List<JoinPart<?,?,?,?>> joins) {
         List<JoinPart<?,?,?,?>> res = new ArrayList<>();
        for (JoinPart<?,?,?,?> join : joins) {
            Table<?> table = join.getTable();
            Set<Table<?>> subTables = table.getSubTables();
            if (subTables.isEmpty()) {
                res.add(new JoinPart(join.getTable(), join.getOn(), join.getWhere().orElseGet(null), explode(join.getJoins())));
            } else {
                for (Table<?> subTable : subTables) {
                    res.add(new JoinPart(subTable, join.getOn(), join.getWhere().orElseGet(null), explode(join.getJoins())));
                }
            }
        }
        return res;
    }

}
