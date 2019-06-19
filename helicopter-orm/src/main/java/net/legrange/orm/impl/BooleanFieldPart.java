package net.legrange.orm.impl;

import net.legrange.orm.def.BooleanField;
import net.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class BooleanFieldPart<T extends Table<O>, O> extends FieldPart<T, O, Boolean> implements
        BooleanField<T, O>,
        WithEqualsPart<T, O, Boolean> {

    public BooleanFieldPart(String javaName, String sqlName) {
        super(Boolean.class, javaName, sqlName);
    }

}
