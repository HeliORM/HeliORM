package me.legrange.orm.rep;

import java.util.Optional;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class Query extends TableSpec {

    private Optional<Order> order = Optional.empty();

    public Query(Table table) {
        super(table);
    }

    public Optional<Order> getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = Optional.ofNullable(order);
    }

}
