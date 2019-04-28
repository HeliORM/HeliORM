package me.legrange.orm.rep;

import me.legrange.orm.Field;

/**
 *
 * @author gideon
 */
public class ValueCriteria extends FieldCriteria {

    private final Object value;

    public ValueCriteria(Field field, Object value) {
        super(field);
        this.value = value;
    }

}
