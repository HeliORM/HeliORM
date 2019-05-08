package me.legrange.orm.rep;

import me.legrange.orm.Field;

/**
 *
 * @author gideon
 */
public abstract class FieldCriteria extends Criteria {

    private final Field field;

    public FieldCriteria(Type type, Field field) {
        super(type);
        this.field = field;
    }

    public Field getField() {
        return field;
    }

}
