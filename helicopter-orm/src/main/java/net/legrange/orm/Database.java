package net.legrange.orm;

import java.util.Set;

/**
 *
 * @author gideon
 */
public interface Database {

    String getSqlDatabase();

    Set<Table> getTables();

}
