package me.legrange.orm.impl;

import me.legrange.orm.EnumField;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class EnumFieldPart<T extends Table<O>, O, E extends Enum> extends FieldPart<T, O, E> implements EnumField<T, O, E> {

    public EnumFieldPart(Class<E> typeClass, String javaName, String sqlName) {
        super(typeClass, javaName, sqlName);
    }

}
