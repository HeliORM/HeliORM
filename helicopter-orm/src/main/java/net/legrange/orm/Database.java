package net.legrange.orm;

import java.util.List;

/**
 *
 * @author gideon
 */
public interface Database {

    String getSqlDatabase();

    List<Table> getTables();

}
