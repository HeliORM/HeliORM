package me.legrange.orm.rep;

import java.util.Optional;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class Query {

    private final Table table;
    private Optional<Criteria> criteria;
    private Optional<Link> link;
    private Optional<Order> order;

    public Query(Table table) {
        this.table = table;
    }

}
