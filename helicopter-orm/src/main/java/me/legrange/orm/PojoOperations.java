package me.legrange.orm;

/**
 *
 * @author gideon
 */
interface PojoOperations {

    Object newPojoInstance(Table table) throws OrmException;

    void setValue(Object pojo, Field field, Object value) throws OrmException;

    Object getValue(Object pojo, Field field) throws OrmException;
}
