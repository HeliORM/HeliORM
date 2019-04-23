package me.legrange.orm.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import me.legrange.orm.Executable;
import me.legrange.orm.Field;
import me.legrange.orm.Ordered;
import me.legrange.orm.OrmException;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class OrderedPart<T extends Table<O>, O> extends Part implements Ordered<T, O>, Executable<T, O> {

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

    @Override
    public List<O> list() throws OrmException {
        return getOrm().list(this);
    }

    @Override
    public Stream<O> stream() throws OrmException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public O one() throws OrmException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional<O> oneOrNone() throws OrmException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Direction getDirection() {
        return direction;
    }

    public Field<T, O, ?> getField() {
        return field;
    }

}
