package com.heliorm;

import com.heliorm.def.Field;

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
    Object newPojoInstance(Table table) throws OrmException;

    /**
     * Set the value of the given field to the given value on the given POJO.
     *
     * @param pojo  The POJO on which to set the value
     * @param field The field
     * @param value The value to set
     * @throws OrmException Thrown if there is an error setting the value
     */
    void setValue(Object pojo, Field field, Object value) throws OrmException;

    /**
     * Read the value of the given field on the given POJO.
     *
     * @param pojo  The POJO from which to read the value
     * @param field The field
     * @return The value read
     * @throws OrmException Thrown if there is an error reading the value
     */
    Object getValue(Object pojo, Field field) throws OrmException;

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
    int compareTo(Object pojo1, Object pojo2, Field field) throws OrmException;

}
