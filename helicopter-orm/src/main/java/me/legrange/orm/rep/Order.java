package me.legrange.orm.rep;

import java.util.Optional;
import me.legrange.orm.Field;

/**
 *
 * @author gideon
 */
public class Order {

    public enum Direction {
        ASCENDING, DESCENDING;
    }

    private final Field field;
    private final Direction direction;
    private Optional<Order> thenBy;

    public Order(Field field, Direction direction) {
        this.field = field;
        this.direction = direction;
    }

}
