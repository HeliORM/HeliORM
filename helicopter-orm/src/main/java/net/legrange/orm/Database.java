package net.legrange.orm;

import java.util.List;

/**
 * The interface that must be implemented to define a database for use by the
 * ORM
 *
 * @author gideon
 */
public interface Database {

    /**
     * Return the SQL database name.
     *
     * @return The SQL name
     */
    String getSqlDatabase();

    /**
     * Return the tables associated with this database.
     *
     * @return The tables
     */
    List<Table> getTables();

}
