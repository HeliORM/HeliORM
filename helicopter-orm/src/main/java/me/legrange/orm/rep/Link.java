package me.legrange.orm.rep;

import java.util.Optional;
import me.legrange.orm.Field;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class Link {

    private final Table table;
    private final Field leftField;
    private final Field field;
    private Optional<Criteria> criteria;

    public Link(Table table, Field leftField, Field field) {
        this.table = table;
        this.leftField = leftField;
        this.field = field;
    }

}
