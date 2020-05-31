package net.legrange.orm.driver;

import net.legrange.orm.Table;

public interface TableGenerator {

    String generateSql(Table<?> table) throws OrmSqlException;

}
