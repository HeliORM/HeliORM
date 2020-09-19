package com.heliorm;

import com.heliorm.def.Executable;
import com.heliorm.def.Select;
import com.heliorm.impl.Part;
import com.heliorm.impl.SelectPart;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

/**
 * The object relational mapper. This is the class that provides the user
 * functionality. It provides methods to query the database to return POJOs, as
 * well as to create update and delete POJOs.
 *
 * @author gideon
 */
public final class Orm implements AutoCloseable {


    private final OrmDriver driver;
    private final Map<Class<?>, Table> tables = new HashMap();

    /**
     * Create an ORM mapper using the supplied driver instance. This is meant to
     * be used with third party drivers.
     *
     * @param driver The driver used to access data.
     * @throws OrmException
     */
    public Orm(OrmDriver driver) throws OrmException {
        this.driver = driver;
    }

    /**
     * Persist a new POJO to the database.
     *
     * @param <O>  The type of the POJO
     * @param pojo The POJO to persist
     * @return The updated POJO
     * @throws OrmException Thrown if there is an error creating the POJO
     */
    public <O> O create(O pojo) throws OrmException {
        if (pojo == null) {
            throw new OrmException("Attempt to create a null POJO");
        }
        return driver.create(tableFor(pojo), pojo);
    }

    /**
     * Persist an existing POJO to the database.
     *
     * @param <O>  The type of the POJO
     * @param pojo The POJO to persist
     * @return The updated POJO
     * @throws OrmException Thrown if there is an error updating the POJO
     */
    public <O> O update(O pojo) throws OrmException {
        if (pojo == null) {
            throw new OrmException("Attempt to update a null POJO");
        }
        return driver.update(tableFor(pojo), pojo);
    }

    /**
     * Delete a POJO from the database.
     *
     * @param <O>  The type of the POJO
     * @param pojo The POJO to delete
     * @throws OrmException Thrown if there is an error deleting the POJO
     */
    public <O> void delete(O pojo) throws OrmException {
        if (pojo == null) {
            throw new OrmException("Attempt to delete a null POJO");
        }
        driver.delete(tableFor(pojo), pojo);
    }

    /**
     * Create a query builder pattern to select data from the database.
     *
     * @param <T>   The type of the table to select on
     * @param <O>   The type of the POJOs selected
     * @param table The table to use as starting point for building the query.
     * @return The Select object that can be used to expand the query or to
     * query data.
     */
    public <T extends Table<O>, O> Select<T, O, T, O> select(T table) {
        return new SelectPart(null, table, this);
    }

    /** Open a new transaction.
     *
     * @return The transaction
     * @throws OrmException Thrown if the driver doesn't support transactions of if there is a problem opening one
     */
    public OrmTransaction openTransaction() throws OrmException {
        if (!(driver instanceof OrmTransactionDriver)) {
            throw new OrmTransactionException("The ORM driver does not support transactions");
        }
        return ((OrmTransactionDriver) driver).openTransaction();
    }

    @Override
    public void close() throws OrmException {
    }

    /**
     * Find the table definition for the given POJO.
     *
     * @param <O>  The type of the POJO
     * @param pojo The pojo
     * @return The table
     * @throws OrmException Thrown if there is an finding the table the POJO
     */
    public <O> Table<O> tableFor(O pojo) throws OrmException {
        if (pojo == null) {
            throw new OrmException("Attempt to do table lookup for a null POJO");
        }
        if (tables.isEmpty()) {
            ServiceLoader<Database> svl = ServiceLoader.load(Database.class);
            for (Database database : svl) {
                for (Table table : database.getTables()) {
                    tables.put(table.getObjectClass(), table);
                }
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
     * @param <O>  The type of Pojo to return
     * @param <P>  The type of programmed query
     * @param tail The tail of the query
     * @return The list of loaded Pojos
     * @throws OrmException Thrown if any of the large number of things that can
     *                      go wrong did go wrong.
     */
    public <O, P extends Part & Executable> List<O> list(P tail) throws OrmException {
        try (Stream<O> stream = stream(tail)) {
            return stream.collect(Collectors.toList());
        }
    }

    /**
     * Execute the supplied programmed query and return a stream of result
     * pojos.
     *
     * @param <O>  The type of Pojo to return
     * @param <P>  The type of programmed query
     * @param tail The tail of the query
     * @return The stream of loaded Pojos
     * @throws OrmException Thrown if any of the large number of things that can
     *                      go wrong did go wrong.
     */
    public <O, P extends Part & Executable> Stream<O> stream(P tail) throws OrmException {
        return driver.stream(tail);
    }

    /**
     * Execute the supplied programmed query and return an optional with a
     * possible result. It is expected that either zero or one results will be
     * found, so more than one result will cause an error.
     *
     * @param <O>  The type of Pojo to return
     * @param <P>  The type of programmed query
     * @param tail The tail of the query
     * @return The optional found Pojo
     * @throws OrmException Thrown if any of the large number of things that can
     *                      go wrong did go wrong.
     */
    public <O, P extends Part & Executable> Optional<O> optional(P tail) throws OrmException {
        try (Stream<O> stream = stream(tail)) {
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
    }

    /**
     * Execute the supplied programmed query and return exactly one matching
     * result. It is expected that exactly one result will be found, so no
     * result or more than one result will cause an error.
     *
     * @param <O>  The type of Pojo to return
     * @param <P>  The type of programmed query
     * @param tail The tail of the query
     * @return The found Pojo
     * @throws OrmException Thrown if any of the large number of things that can
     *                      go wrong did go wrong.
     */
    public <O, P extends Part & Executable> O one(P tail) throws OrmException {
        try (Stream<O> stream = stream(tail)) {
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

}
