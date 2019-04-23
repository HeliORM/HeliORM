package me.legrange.orm.impl;

import me.legrange.orm.StringField;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class BooleanFieldPart<T extends Table<O>, O> extends FieldPart<T, O, String> implements StringField<T, O> {

    public BooleanFieldPart(String javaName, String sqlName) {
        super(String.class, javaName, sqlName);
    }

}
