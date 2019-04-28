package me.legrange.orm.rep;

import me.legrange.orm.Field;

/**
 *
 * @author gideon
 */
abstract class FieldCriteria extends Criteria {

    private final Field field;

    public FieldCriteria(Field field) {
        super(Type.FIELD);
        this.field = field;
    }

}
