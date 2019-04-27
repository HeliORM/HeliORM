package me.legrange.orm.impl;

import static java.lang.String.format;
import java.util.List;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class ListExpressionPart<T extends Table<O>, O, C> extends ExpressionPart<T, O, C> {

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

    public Operator getOperator() {
        return operator;
    }

    public List<C> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return format("%s (%s)", operator.name(), values);
    }
}
