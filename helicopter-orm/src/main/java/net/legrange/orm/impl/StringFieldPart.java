package net.legrange.orm.impl;

import net.legrange.orm.StringField;
import net.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class StringFieldPart<T extends Table<O>, O> extends FieldPart<T, O, String> implements
        StringField<T, O>,
        WithEqualsPart<T, O, String>,
        WithRangePart<T, O, String>,
        WithInPart<T, O, String>,
        WithLikePart<T, O, String> {

    public StringFieldPart(String javaName, String sqlName) {
        super(String.class, javaName, sqlName);
    }

    public StringFieldPart(String javaName, String sqlName, boolean primaryKey) {
        super(String.class, javaName, sqlName, primaryKey);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.STRING;
    }

}
