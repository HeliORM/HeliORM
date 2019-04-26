package me.legrange.orm.impl;

import java.util.List;
import me.legrange.orm.ExpressionContinuation;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class ListExpressionPart<T extends Table<O>, O, C> extends Part<T, O, T, O> implements ExpressionContinuation<T, O> {

    private final Operator operator;
    private final List<C> values;

    public enum Operator {
        IN, NOT_IN;
    }

    public ListExpressionPart(Part left, Operator op, List<C> values) {
        super(left);
        this.operator = op;
        this.values = values;
    }

    @Override
    public Type getType() {
        return Type.LIST_EXPRESSION;
    }

    @Override
    public ExpressionContinuation<T, O> and(ExpressionContinuation<T, O> expr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ExpressionContinuation<T, O> or(ExpressionContinuation<T, O> expr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
