package net.legrange.orm.query;

import java.util.Optional;
import net.legrange.orm.def.Field;

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
    private Optional<Order> thenBy = Optional.empty();

    public Order(Field field, Direction direction) {
        this.field = field;
        this.direction = direction;
    }

    public Field getField() {
        return field;
    }

    public Direction getDirection() {
        return direction;
    }

    public Optional<Order> getThenBy() {
        return thenBy;
    }

    public void setThenBy(Order thenBy) {
        this.thenBy = Optional.ofNullable(thenBy);
    }

}
