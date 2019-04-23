package me.legrange.orm.impl;

import me.legrange.orm.StringField;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class StringFieldPart<T extends Table<O>, O> extends FieldPart<T, O, String> implements StringField<T, O> {

    public StringFieldPart(String javaName, String sqlName) {
        super(String.class, javaName, sqlName);
    }

}
