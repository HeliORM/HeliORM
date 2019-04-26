package me.legrange.orm.impl;

import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class BooleanFieldPart<T extends Table<O>, O> extends FieldPart<T, O, Boolean> {

    public BooleanFieldPart(String javaName, String sqlName) {
        super(Boolean.class, javaName, sqlName);
    }

}
