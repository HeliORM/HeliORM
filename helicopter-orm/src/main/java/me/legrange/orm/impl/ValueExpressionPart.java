package me.legrange.orm.impl;

import static java.lang.String.format;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 * @param <T>
 * @param <O>
 * @param <C>
 */
public class ValueExpressionPart<T extends Table<O>, O, C> extends ExpressionPart<T, O, C> {

    private final Operator operator;
    private final C value;

    public enum Operator {
        EQ, NOT_EQ, LT, LE, GT, GE, LIKE, NOT_LIKE;
    }

    public ValueExpressionPart(FieldPart left, Operator op, C value) {
        super(left);
        this.operator = op;
        this.value = value;
    }

    @Override
    public Type getType() {
        return Type.VALUE_EXPRESSION;
    }

    public Operator getOperator() {
        return operator;
    }

    public C getValue() {
        return value;
    }

    @Override
    public String toString() {
        return format("%s '%s'", operator.name(), value);
    }

}
