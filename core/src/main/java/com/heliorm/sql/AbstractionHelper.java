package com.heliorm.sql;

import com.heliorm.Table;
import com.heliorm.impl.JoinPart;
import com.heliorm.impl.OrderedPart;
import com.heliorm.impl.Part;
import com.heliorm.impl.SelectPart;

import java.util.*;

class AbstractionHelper {

    /** Expand the query parts in the list so that the query branches into
     * multiple table queries when a select or join with a table which has
     * sub-tables is encountered.
     * @param tail part
     * @return The expanded query parts lists.
     */
     List<List<Part>> explodeAbstractions(Part tail) {
        return explode(tailToList(tail), 0);
    }

    /**
     * Create a comparator based on the tail of the query that will compare
     * Pojos on their fields.
     *
     * @param <O>
     * @param <P>
     * @param tail
     * @return
     */
     <O, P extends Part> Comparator<PojoCompare<O>> makeComparatorForTail(P tail) {
        List<OrderedPart> order = new LinkedList();
        while (tail.getType() == Part.Type.ORDER) {
            order.add(0, (OrderedPart) tail);
            tail = (P) tail.left();
        }
        List<Comparator<PojoCompare<O>>> comps = new LinkedList();
        for (OrderedPart op : order) {
            comps.add((Comparator<PojoCompare<O>>) (PojoCompare<O> w1, PojoCompare<O> w2) -> {
                if (op.getDirection() == OrderedPart.Direction.ASCENDING) {
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


    /**
     * Expand the query parts in the list so that the query branches into
     * multiple table queries when a select or join with a table which has
     * sub-tables is encountered.
     *
     * @param parts the query parts
     * @param idx   the index of the part being examined
     * @return The expanded query parts lists.
     */
    private List<List<Part>> explode(List<Part> parts, int idx) {
        Part part = parts.get(idx);
        List<List<Part>> res = new LinkedList();
        if ((part.getType() == Part.Type.SELECT) || (part.getType() == Part.Type.JOIN)) {
            Table<?> table = part.getSelectTable();
            Set<Table<?>> subTables = table.getSubTables();
            if (!subTables.isEmpty()) {
                for (Table<?> subTable : subTables) {
                    List<Part> copy = new ArrayList(parts);
                    Part old = copy.remove(idx);
                    Part left = null;
                    if (idx > 0) {
                        left = old.left();
                    }
                    Part newPart;
                    if (part.getType() == Part.Type.SELECT) {
                        copy.add(idx, new SelectPart(left, subTable));
                    } else {
                        copy.add(idx, new JoinPart(left, subTable));
                    }
                    if (idx < parts.size() - 1) {
                        res.addAll(explode(copy, idx + 1));
                    } else {
                        res.add(copy);
                    }
                }
                return res;
            }
        }
        if (idx < parts.size() - 1) {
            res.addAll(explode(parts, idx + 1));
        } else {
            res.add(parts);
        }
        return res;
    }
}
