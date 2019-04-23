package me.legrange.orm.impl;

import me.legrange.orm.Field;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
abstract class FieldPart<T extends Table<O>, O, C> implements Field<T, O, C> {

    private final Class<C> typeClass;
    private final String javaName;
    private final String sqlName;

    protected FieldPart(Class<C> typeClass, String javaName, String sqlName) {
        this.typeClass = typeClass;
        this.javaName = javaName;
        this.sqlName = sqlName;
    }

    @Override
    public final String getJavaName() {
        return javaName;
    }

    @Override
    public final String getSqlName() {
        return sqlName;
    }

    @Override
    public final Class<C> getJavaType() {
        return typeClass;
    }

}
