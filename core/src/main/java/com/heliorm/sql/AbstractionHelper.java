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
class AbstractionHelper {

    /**
     * Expand the query parts in the list so that the query branches into
     * multiple table queries when a select or join with a table which has
     * sub-tables is encountered.
     *
     * @param tail part
     * @return The expanded query parts lists.
     */
    List<? extends ExecutablePart<?>> explodeAbstractions(ExecutablePart<?> tail) {
        if (tail instanceof SelectPart<?>) {
            return explode(tail.getSelect());
        }
        if (tail instanceof OrderedPart<?>) {
            return explode((OrderedPart<?>) tail);
        }
        return Collections.emptyList();
    }

    /**
     * Create a comparator based on the tail of the query that will compare
     * Pojos on their fields.
     *
     * @param <O>
     * @return
     */
    <T extends Table<O>, O> Comparator<PojoCompare<O>> makeComparatorForTail(List<OrderPart<O>> order) {
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

    private List<OrderedPart<?>> explode(OrderedPart<?> ordered) {
        List<OrderedPart<?>> res = new ArrayList<>();
        SelectPart<?> select = ordered.getSelect();
        Table<?> table = select.getTable();
        Set<Table<?>> subTables = table.getSubTables();
        if (subTables.isEmpty()) {
            SelectPart selectPart = new SelectPart(select.getSelector(), select.getTable(), select.getWhere(), explode(select.getJoins()));
            res.add(new OrderedPart(select.getSelector(), selectPart,
                    ordered.getOrder(), ordered.getLimit()));
        } else {
            for (Table<?> subTable : subTables) {
                res.add(new OrderedPart(select.getSelector(),
                        new SelectPart(select.getSelector(), subTable, select.getWhere(), explode(select.getJoins())),
                        ordered.getOrder(), ordered.getLimit()));
            }
        }
        return res;
    }

    private List<SelectPart<?>> explode(SelectPart<?> select) {
        List<SelectPart<?>> res = new ArrayList<>();
        Table<?> table = select.getTable();
        Set<Table<?>> subTables = table.getSubTables();
        if (subTables.isEmpty()) {
            res.add(new SelectPart(select.getSelector(), select.getTable(), select.getWhere(), explode(select.getJoins()), select.getOrder(), select.getLimit()));
        } else {
            for (Table<?> subTable : subTables) {
                res.add(new SelectPart(select.getSelector(), subTable, select.getWhere(), explode(select.getJoins()), select.getOrder(), select.getLimit()));
            }
        }
        return res;
    }

    private List<JoinPart<?, ?>> explode(List<JoinPart<?, ?>> joins) {
        List<JoinPart<?, ?>> res = new ArrayList<>();
        for (JoinPart<?, ?> join : joins) {
            Table<?> table = join.getTable();
            Set<Table<?>> subTables = table.getSubTables();
            if (subTables.isEmpty()) {
                res.add(new JoinPart(join.getTable(), join.getOn(), join.getWhere(), explode(join.getJoins())));
            } else {
                for (Table<?> subTable : subTables) {
                    res.add(new JoinPart(subTable, join.getOn(), join.getWhere(), explode(join.getJoins())));
                }
            }
        }
        return res;
    }

}
