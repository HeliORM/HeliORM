package com.heliorm.sql;

import com.heliorm.Table;

/**
 * Interface to implement generation of SQL schemas.
 */
public interface TableGenerator {

    /**
     * Generate a SQL table schema
     *
     * @param table The table for which to generate the schema
     * @return The schema
     * @throws OrmSqlException Throws if there is an error generating the schema
     */
    <O> String generateSchema(Table<O> table) throws OrmSqlException;

}
