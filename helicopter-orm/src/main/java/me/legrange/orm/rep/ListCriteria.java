package me.legrange.orm.rep;

import java.util.List;
import me.legrange.orm.Field;

/**
 *
 * @author gideon
 */
public class ListCriteria extends FieldCriteria {

    private final List values;

    public ListCriteria(Field field, List values) {
        super(field);
        this.values = values;
    }

}
