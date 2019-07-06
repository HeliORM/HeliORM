package net.legrange.orm.mojo;

import java.util.Set;
import net.legrange.orm.Database;
import net.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public interface Generator<T extends Table> {

    public enum PojoStrategy {
        annotated;
    }

    Set<Class<?>> getAllPojoClasses() throws GeneratorException;

    T getPojoModel(Class clazz, Database database, Set<T> subTables);

}
