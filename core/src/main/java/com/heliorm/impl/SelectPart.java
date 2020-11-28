package com.heliorm.impl;

import com.heliorm.Orm;
import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.ExpressionContinuation;
import com.heliorm.def.Field;
import com.heliorm.def.Join;
import com.heliorm.def.OnClause;
import com.heliorm.def.Ordered;
import com.heliorm.def.Select;

import static java.lang.String.format;

/**
 *
 * @author gideon
 */
public class SelectPart<LT extends Table<LO>, LO, RT extends Table<RO>, RO> extends ExecutablePart<LT, LO>
        implements Select<LT, LO, RT, RO> {

    private final Orm orm;
    private final Table<?> table;

    public SelectPart(Part left, Table table) {
        this(left, table, null);
    }

    public SelectPart(Part left, Table table, Orm orm) {
        super(left);
        this.table = table;
        this.orm = orm;
    }

    @Override
    protected Orm getOrm() {
        if (left() != null) {
            return left().getOrm();
        }
        return orm;
    }

    @Override
    public final Table getReturnTable() {
        if (left() != null) {
            return left().getReturnTable();
        }
        return table;
    }

    @Override
    public Table getSelectTable() {
        return table;
    }

    @Override
    public <RT extends Table<RO>, RO> Join<LT, LO, RT, RO> join(RT table) {
        return new JoinPart(this, table);
    }

    @Override
    public <RT extends  Table<RO>, RO> OnClause<LT, LO, RT, RO> joinOnKey(RT table) throws OrmException {
        Join<LT, LO, RT, RO> join = join(table);
        for (Field<RT,RO, ?> rightField : table.getFields()) {
            if (rightField.isForeignKey()) {
                Table<?> other = rightField.getForeignTable().get();
                if (other.equals(left().getSelectTable())) {
                    return join.on(other.getPrimaryKey().get(), rightField);
                }
            }
        }
        for (Field<LT,LO, ?> leftField : this.table.getFields()) {
            if (leftField.isForeignKey()) {
                Table<?> other = leftField.getForeignTable().get();
                if (other.equals(table)) {
                    return join.on(leftField, table.getPrimaryKey().get());
                }
            }
        }
        throw new OrmException(format("Cannot find primary key and foreign key for %s and %s",
                left().getSelectTable().getObjectClass().getSimpleName(),
                table.getObjectClass().getSimpleName()));
    }

    @Override
    public Continuation<LT, LO, RT, RO> where(ExpressionContinuation<RT, RO> cont) {
        return new ContinuationPart(this, Type.WHERE, cont);
    }

    @Override
    public <F extends Field<LT, LO, C>, C> Ordered<LT, LO> orderBy(F field) {
        return new OrderedPart(this, OrderedPart.Direction.ASCENDING, field);
    }

    @Override
    public <F extends Field<LT, LO, C>, C> Ordered<LT, LO> orderByDesc(F field) {
        return new OrderedPart(this, OrderedPart.Direction.DESCENDING, field);
    }

    @Override
    public Type getType() {
        return Type.SELECT;
    }

    @Override
    public String toString() {
        return format("%s %s", getType().name(), table.getSqlTable());
    }

}
