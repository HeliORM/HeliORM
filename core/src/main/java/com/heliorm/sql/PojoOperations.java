package com.heliorm.sql;

import com.heliorm.Field;
import com.heliorm.OrmException;
import com.heliorm.Table;

/**
 * This interface defines the operations on POJOs required for the ORM to
 * manipulate them generically to function. The idea is that we can support
 * different methodologies for manipulating POJOs for different use cases. For
 * example, a BeanPojoOperations class could use bean patterns.
 *
 * @author gideon
 */
public interface PojoOperations {

    /**
     * Create a new POJO of the type supported by the given table.
     *
     * @param table The table
     * @return The new POJO instance
     * @throws OrmException Thrown if there is an error creating a new POJO
     */
    <O> O newPojoInstance(Table<O> table) throws OrmException;

    /**
     * Set the value of the given field to the given value on the given POJO.
     *
     * @param pojo  The POJO on which to set the value
     * @param field The field
     * @param value The value to set
     * @throws OrmException Thrown if there is an error setting the value
     */
    <O> void setValue(Object pojo, Field<O, ?> field, Object value) throws OrmException;

    /**
     * Read the value of the given field on the given POJO.
     *
     * @param pojo  The POJO from which to read the value
     * @param field The field
     * @return The value read
     * @throws OrmException Thrown if there is an error reading the value
     */
    <O> Object getValue(Object pojo, Field< O, ?> field) throws OrmException;

    /**
     * Compare the given field value on two POJOs
     *
     * @param pojo1 The first POJO
     * @param pojo2 The second POJO
     * @param field The field of which the value on each POJO must be used to
     *              compare them
     * @return A comparator compatible comparison result
     * @throws OrmException Thrown if there is an error comparing the two fields
     */
    <O> int compareTo(O pojo1, O pojo2, Field<O, ?> field) throws OrmException;

}
