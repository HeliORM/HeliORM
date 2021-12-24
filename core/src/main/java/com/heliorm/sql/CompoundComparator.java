package com.heliorm.sql;

import java.util.Comparator;
import java.util.List;

/**
 * A comparator that compares two objects based on a list of supplied comparators.
 *
 * @author gideon
 */
final class CompoundComparator<O> implements Comparator<O> {

    private final List<Comparator<O>> comps;

    public CompoundComparator(List<Comparator<O>> comps) {
        this.comps = comps;
    }

    @Override
    public int compare(O o1, O o2) {
        for (Comparator<O> comp : comps) {
            int res = comp.compare(o1, o2);
            if (res != 0) {
                return res;
            }
        }
        return 0;
    }

}
