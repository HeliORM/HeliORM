package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.ExpressionContinuation;
import com.heliorm.def.Field;
import com.heliorm.def.Join;
import com.heliorm.def.OnClause;
import com.heliorm.def.Ordered;

import static java.lang.String.format;

public class ContinuationPart<LT extends Table<LO>, LO, RT extends Table<RO>, RO> extends ExecutablePart<LT, LO> implements Continuation<LT, LO, RT, RO> {

    private final Part expression;
    private final Type type;

    ContinuationPart(Part left, Type type, ExpressionContinuation expr) {
        super(left);
        this.expression = ((Part) expr).head();
        this.type = type;
    }

    @Override
    public Continuation<LT, LO, RT, RO> and(ExpressionContinuation<RT, RO> cont) {
        return new ContinuationPart(this, Type.AND, cont);
    }

    @Override
    public Continuation<LT, LO, RT, RO> or(ExpressionContinuation<RT, RO> cont) {
        return new ContinuationPart(this, Type.OR, cont);
    }

    @Override
    public <RT extends Table<RO>, RO> Join<LT, LO, RT, RO> join(RT table) {
        return new JoinPart(this, table);
    }

    @Override
    public  OnClause<LT, LO, RT, RO> joinOnKey(RT table) throws OrmException {
        Join<LT, LO, RT, RO> join = join(table);
        for (Field<RT,RO, ?> rightField : table.getFields()) {
            if (rightField.isForeignKey()) {
                Table<?> other = rightField.getForeignTable().get();
                if (other.equals(left().getSelectTable())) {
                    return join.on( other.getPrimaryKey().get(), rightField);
                }
            }
        }
        Table<LT> selectTable = left().getSelectTable();
        for (Field<LT,LO, ?> leftField : selectTable.getFields()) {
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
    public <F extends Field<LT, LO, C>, C> Ordered<LT, LO> orderBy(F field) {
        return new OrderedPart(this, OrderedPart.Direction.ASCENDING, field);
    }

    @Override
    public <F extends Field<LT, LO, C>, C> Ordered<LT, LO> orderByDesc(F field) {
        return new OrderedPart(this, OrderedPart.Direction.DESCENDING, field);
    }

    @Override
    public Type getType() {
        return type;
    }

    public Part getExpression() throws OrmException {
        return expression;
    }

    @Override
    public String toString() {
        return format("%s %s", getType().name(), expression);
    }

}
