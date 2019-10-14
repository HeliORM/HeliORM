package net.legrange.orm;

/**
 * Extends the ORM driver to add database structure manipulation support.
 *
 * @author gideon
 */
public interface OrmMetaDriver extends OrmDriver {

    /**
     * Check if the given table exists in the database.
     *
     * @param table
     * @return
     * @throws OrmException
     */
    boolean tableExists(Table table) throws OrmException;

}
