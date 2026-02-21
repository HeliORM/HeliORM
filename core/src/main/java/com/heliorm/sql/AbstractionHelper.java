package com.heliorm.sql;

import com.heliorm.Table;
import com.heliorm.impl.ExecutablePart;
import com.heliorm.impl.JoinPart;
import com.heliorm.impl.OrderPart;
import com.heliorm.impl.OrderedPart;
import com.heliorm.impl.SelectPart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Helper class that deals with creating concrete selection hierarchies from ones containing abstract classes.
 */
final class AbstractionHelper {

    /**
     * Expand the query parts in the list so that the query branches into
     * multiple table queries when a select or join with a table which has
     * sub-tables is encountered.
     *
     * @param tail part
     * @return The expanded query parts lists.
     */
   static <O> List<? extends ExecutablePart<O>> explodeAbstractions(ExecutablePart<O> tail) {
        if (tail instanceof SelectPart<O> sp) {
            return explode(sp.getSelect());
        }
        if (tail instanceof OrderedPart<O> op) {
            return explode(op);
        }
        return Collections.emptyList();
    }

    /**
     * Create a comparator based on the tail of the query that will compare
     * Pojos on their fields.
     *
     */
    static <O> Comparator<PojoCompare<O>> makeComparatorForTail(List<OrderPart<O>> order) {
        List<Comparator<PojoCompare<O>>> comps = new LinkedList<>();
        for (OrderPart<O> op : order) {
            comps.add((PojoCompare<O> w1, PojoCompare<O> w2) -> {
                if (op.getDirection() == OrderPart.Direction.ASCENDING) {
                    return w1.compareTo(w2, op.getField());
                } else {
                    return w2.compareTo(w1, op.getField());

                }
            });
        }
        return new CompoundComparator<>(comps);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <O> List<OrderedPart<O>> explode(OrderedPart<O> ordered) {
        var res = new ArrayList<OrderedPart<O>>();
        var select = ordered.getSelect();
        var  table = select.getTable();
        Set<Table<?>> subTables = table.getSubTables();
        if (subTables.isEmpty()) {
            var selectPart = new SelectPart<>(select.getSelector(), select.getTable(), select.getWhere().orElse(null), explode(select.getJoins()));
            res.add(new OrderedPart(select.getSelector(), selectPart,
                    ordered.getOrder(), ordered.getLimit()));
        } else {
            for (var subTable : subTables) {
                res.add(new OrderedPart(select.getSelector(),
                        new SelectPart(select.getSelector(), subTable, select.getWhere().orElse(null), explode(select.getJoins())),
                        ordered.getOrder(), ordered.getLimit()));
            }
        }
        return res;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <O> List<SelectPart<O>> explode(SelectPart<O> select) {
        List<SelectPart<O>> res = new ArrayList<>();
        Table<?> table = select.getTable();
        Set<Table<?>> subTables = table.getSubTables();
        if (subTables.isEmpty()) {
            res.add(new SelectPart(select.getSelector(), select.getTable(), select.getWhere().orElse(null), explode(select.getJoins()), select.getOrder(), select.getLimit()));
        } else {
            for (Table<?> subTable : subTables) {
                res.add(new SelectPart(select.getSelector(), subTable, select.getWhere().orElse(null), explode(select.getJoins()), select.getOrder(), select.getLimit()));
            }
        }
        return res;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static List<JoinPart<?, ?>> explode(List<JoinPart<?, ?>> joins) {
        List<JoinPart<?, ?>> res = new ArrayList<>();
        for (JoinPart<?, ?> join : joins) {
            Table<?> table = join.getTable();
            Set<Table<?>> subTables = table.getSubTables();
            if (subTables.isEmpty()) {
                res.add(new JoinPart(join.getTable(), join.getOn(), join.getWhere().orElse(null), explode(join.getJoins())));
            } else {
                for (Table<?> subTable : subTables) {
                    res.add(new JoinPart(subTable, join.getOn(), join.getWhere().orElse(null), explode(join.getJoins())));
                }
            }
        }
        return res;
    }

    private AbstractionHelper() {}
}
