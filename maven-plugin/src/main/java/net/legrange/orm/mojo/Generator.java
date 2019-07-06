package net.legrange.orm.mojo;

import java.util.Set;
import net.legrange.orm.Database;
import net.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public interface Generator {

    public enum PojoStrategy {
        annotated;
    }

    Set<Class<?>> getAllPojoClasses() throws GeneratorException;

    Table getPojoModel(Class clazz, Database database);

}
