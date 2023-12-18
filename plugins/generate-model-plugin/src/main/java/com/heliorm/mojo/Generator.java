package com.heliorm.mojo;

import com.heliorm.Database;
import com.heliorm.Table;

import java.util.Set;

/**
 * @param <T> Type of table implementation returned by this generator.
 * @author gideon
 */
public interface Generator<T extends Table> {

    /**
     * Get all the PJO classes detected by this generator.
     *
     * @return The set of POJO classes.
     */
    Set<Class<?>> getAllPojoClasses() throws GeneratorException;

    /**
     * Create a table model for the given POJO class in the given database with
     * the supplied sub-tables.
     *
     * @param clazz     The POJO class
     * @param database  The database
     * @param subTables The set of sub-tables of this table (if any)
     * @return The table
     */
    T getPojoModel(Class clazz, Database database, Set<T> subTables);

    enum PojoStrategy {
        annotated
    }

}
