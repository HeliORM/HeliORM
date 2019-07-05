package net.legrange.orm.mojo;

import java.util.List;
import net.legrange.orm.Database;
import net.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public abstract class Generator {

    public enum PojoStrategy {
        annotated;
    }

    protected final GenerateModel generator;

    public Generator(GenerateModel generator) {
        this.generator = generator;
    }

    public abstract List<Database> getDatabaseModels() throws GeneratorException;

    public abstract List<Table> getPojoModels() throws GeneratorException;

    public abstract Table getPojoModel(Class clazz) throws GeneratorException;

}
