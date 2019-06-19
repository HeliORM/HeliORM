package net.legrange.orm.impl;

import net.legrange.orm.Executable;
import net.legrange.orm.Field;
import net.legrange.orm.Ordered;
import net.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class OrderedPart<T extends Table<O>, O> extends ExecutablePart<T, O> implements Ordered<T, O>, Executable<T, O> {

    public enum Direction {
        ASCENDING, DESCENDING;
    }

    private final Direction direction;
    private final Field<T, O, ?> field;

    public OrderedPart(Part left, Direction direction, Field<T, O, ?> field) {
        super(left);
        this.direction = direction;
        this.field = field;
    }

    @Override
    public Type getType() {
        return Type.ORDER;
    }

    @Override
    public <F extends Field<T, O, C>, C> Ordered<T, O> thenBy(F field) {
        return new OrderedPart(this, Direction.ASCENDING, field);
    }

    @Override
    public <F extends Field<T, O, C>, C> Ordered<T, O> thenByDesc(F field) {
        return new OrderedPart(this, Direction.DESCENDING, field);
    }

    public Direction getDirection() {
        return direction;
    }

    public Field<T, O, ?> getField() {
        return field;
    }

    @Override
    public String toString() {
        return getType().name();
    }

}
