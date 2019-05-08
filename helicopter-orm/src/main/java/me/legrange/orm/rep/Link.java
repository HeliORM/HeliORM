package me.legrange.orm.rep;

import me.legrange.orm.Field;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class Link extends TableSpec {

    private final Field leftField;
    private final Field field;

    public Link(Table table, Field leftField, Field field) {
        super(table);
        this.leftField = leftField;
        this.field = field;
    }

    public Field getLeftField() {
        return leftField;
    }

    public Field getField() {
        return field;
    }

}
