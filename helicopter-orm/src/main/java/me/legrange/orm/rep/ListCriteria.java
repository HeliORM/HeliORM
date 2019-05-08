package me.legrange.orm.rep;

import java.util.List;
import me.legrange.orm.Field;
import me.legrange.orm.rep.ListCriteria.Operator;

/**
 *
 * @author gideon
 */
public class ListCriteria extends FieldCriteria {

    public enum Operator {
        IN, NOT_IN;
    }

    private final List values;
    private final Operator operator;

    public ListCriteria(Field field, Operator operator, List values) {
        super(Type.LIST_FIELD, field);
        this.values = values;
        this.operator = operator;
    }

    public List getValues() {
        return values;
    }

    public Operator getOperator() {
        return operator;
    }

}
