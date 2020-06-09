package net.legrange.orm.driver;

import net.legrange.orm.Table;

/** Interface to implement to generate SQL schemas.  */
public interface TableGenerator {

    /** Generate a SQL table schema
     *
     * @param table The table for which to generate the schema
     * @return The schema
     * @throws OrmSqlException
     */
    String generateSchema(Table<?> table) throws OrmSqlException;

}
