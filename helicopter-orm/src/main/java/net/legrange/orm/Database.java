package net.legrange.orm;

import java.util.List;

/**
 * The interface must be implement to define a database (catalog) for use in the
 * ORM. Instances of this is generated by the meta data generator.
 *
 * @author gideon
 */
public interface Database {

    /**
     * Get the SQL database name for this database.
     *
     * @return The database name
     */
    String getSqlDatabase();

    /**
     * Get the tables in this database.
     *
     * @return The tables
     */
    List<Table> getTables();

}
