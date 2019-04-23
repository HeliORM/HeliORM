package me.legrange.orm;

/**
 *
 * @author gideon
 */
public class AbstractField<T extends Table<O>, O, C> implements Field<T, O, C> {

    private final String javaName;
    private final String sqlName;
    private final Class<C> typeClass;

    public AbstractField(Class<C> typeClass, String javaName, String sqlName) {
        this.javaName = javaName;
        this.sqlName = sqlName;
        this.typeClass = typeClass;
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
