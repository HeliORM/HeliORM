package net.legrange.orm.mojo;

import java.util.Set;
import net.legrange.orm.Database;
import net.legrange.orm.Table;

/**
 *
 * @author gideon
 * @param <T> Type of table implementation returned by this generator.
 */
public interface Generator<T extends Table> {

    public enum PojoStrategy {
        annotated;
    }

    /**
     * Get all the PJO classes detected by this generator.
     *
     * @return The set of POJO classes.
     * @throws GeneratorException
     */
    Set<Class<?>> getAllPojoClasses() throws GeneratorException;

    /**
     * Create a table model for the given POJO class in the given database with
     * the supplied sub-tables.
     *
     * @param clazz The POJO class
     * @param database The database
     * @param subTables The set of sub-tables of this table (if any)
     * @return The table
     */
    T getPojoModel(Class clazz, Database database, Set<T> subTables);

}
