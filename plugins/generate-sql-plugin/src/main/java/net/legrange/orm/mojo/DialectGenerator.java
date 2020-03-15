package net.legrange.orm.mojo;

import net.legrange.orm.Table;

public interface DialectGenerator {

    String generateSql(Table<?> table) throws GeneratorException;

}
