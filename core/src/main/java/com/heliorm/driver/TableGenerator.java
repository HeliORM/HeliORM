package com.heliorm.driver;

import com.heliorm.Table;

/** Interface to implement to generate SQL schemas.  */
public interface TableGenerator {

    /** Generate a SQL table schema
     *
     * @param table The table for which to generate the schema
     * @return The schema
     * @throws OrmSqlException Throws if there is an error generating the schema
     */
    String generateSchema(Table<?> table) throws OrmSqlException;

}
