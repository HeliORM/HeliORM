package net.legrange.orm.def;

import net.legrange.orm.Table;

/**
 *
 * @author gideon
 * @param <T>
 * @param <O>
 * @param <C>
 */
public interface Field<T extends Table<O>, O, C> {

    public enum FieldType {
        LONG, INTEGER, SHORT, BYTE, DOUBLE, FLOAT, BOOLEAN, ENUM, STRING, DATE, TIMESTAMP;
    }

    FieldType getFieldType();

    Class<C> getJavaType();

    String getJavaName();

    String getSqlName();

    boolean isPrimaryKey();

}
