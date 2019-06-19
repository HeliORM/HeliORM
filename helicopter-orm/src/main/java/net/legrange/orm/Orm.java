package net.legrange.orm;

import static java.lang.String.format;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.legrange.orm.def.Executable;
import net.legrange.orm.def.Select;
import net.legrange.orm.driver.MySqlDriver;
import net.legrange.orm.impl.Part;
import net.legrange.orm.impl.SelectPart;

/**
 * @author gideon
 */
public final class Orm implements AutoCloseable {

    public enum Driver {
        MYSQL;
    }

    private final OrmDriver driver;
    private final Map<Class<?>, Table> tables = new HashMap();

    public static Orm open(Connection con, Driver driver) throws OrmException {
        switch (driver) {
            case MYSQL:
                return new Orm(new MySqlDriver(() -> con, new UnsafeFieldOperation()));
            default:
                throw new OrmException(format("Unsupported database type %s. BUG!", driver));
        }
    }

    public Orm(OrmDriver driver) throws OrmException {
        this.driver = driver;
    }

    public <O> O create(O pojo) throws OrmException {
        return driver.create(tableFor(pojo), pojo);
    }

    public <O> O update(O pojo) throws OrmException {
        return driver.update(tableFor(pojo), pojo);
    }

    public <O> void delete(O pojo) throws OrmException {
        driver.delete(tableFor(pojo), pojo);
    }

    public <T extends Table<O>, O> Select<T, O, T, O> select(T table) {
        return new SelectPart(null, table, this);
    }

    @Override
    public void close() throws OrmException {
    }

    public <O> Table<O> tableFor(O pojo) throws OrmException {
        if (tables.isEmpty()) {
            ServiceLoader<Table> svl = ServiceLoader.load(Table.class);
            for (Table table : svl) {
                tables.put(table.getObjectClass(), table);
            }
        }
        Table<O> table = tables.get(pojo.getClass());
        if (table == null) {
            throw new OrmException("Cannot find table for pojo of type " + pojo.getClass().getCanonicalName());
        }
        return table;
    }

    /**
     * Execute the supplied programmed query and return a list of result pojos.
     *
     * @param <O> The type of Pojo to return
     * @param <P> The type of programmed query
     * @param tail The tail of the query
     * @return The list of loaded Pojos
     * @throws OrmException Thrown if any of the large number of things that can
     * go wrong did go wrong.
     */
    public <O, P extends Part & Executable> List<O> list(P tail) throws OrmException {
        return ((Stream<O>) stream(tail)).collect(Collectors.toList());
    }

    /**
     * Execute the supplied programmed query and return a stream of result
     * pojos.
     *
     * @param <O> The type of Pojo to return
     * @param <P> The type of programmed query
     * @param tail The tail of the query
     * @return The stream of loaded Pojos
     * @throws OrmException Thrown if any of the large number of things that can
     * go wrong did go wrong.
     */
    public <O, P extends Part & Executable> Stream<O> stream(P tail) throws OrmException {
        return driver.stream(tail);
    }

    /**
     * Execute the supplied programmed query and return an optional with a
     * possible result. It is expected that either zero or one results will be
     * found, so more than one result will cause an error.
     *
     * @param <O> The type of Pojo to return
     * @param <P> The type of programmed query
     * @param tail The tail of the query
     * @return The optional found Pojo
     * @throws OrmException Thrown if any of the large number of things that can
     * go wrong did go wrong.
     */
    public <O, P extends Part & Executable> Optional<O> oneOrNone(P tail) throws OrmException {
        Stream<O> stream = stream(tail);
        O one;
        Iterator<O> iterator = stream.iterator();
        if (iterator.hasNext()) {
            one = iterator.next();
        } else {
            return Optional.empty();
        }
        if (iterator.hasNext()) {
            throw new OrmException(format("Required one or none %s but found more than one", tail.getReturnTable().getObjectClass().getSimpleName()));
        }
        return Optional.of(one);
    }

    /**
     * Execute the supplied programmed query and return exactly one matching
     * result. It is expected that exactly one result will be found, so no
     * result or more than one result will cause an error.
     *
     * @param <O> The type of Pojo to return
     * @param <P> The type of programmed query
     * @param tail The tail of the query
     * @return The found Pojo
     * @throws OrmException Thrown if any of the large number of things that can
     * go wrong did go wrong.
     */
    public <O, P extends Part & Executable> O one(P tail) throws OrmException {
        Stream<O> stream = stream(tail);
        Iterator<O> iterator = stream.iterator();
        O one;
        if (iterator.hasNext()) {
            one = iterator.next();
        } else {
            throw new OrmException(format("Required exactly one %s but found none", tail.getReturnTable().getObjectClass().getSimpleName()));
        }
        if (iterator.hasNext()) {
            throw new OrmException(format("Required exactly one %s but found more than one", tail.getReturnTable().getObjectClass().getSimpleName()));
        }
        return one;
    }

}
