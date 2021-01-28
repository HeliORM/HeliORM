package com.heliorm.sql;

import com.heliorm.Orm;
import com.heliorm.def.Select;
import com.heliorm.impl.SelectPart;
import com.heliorm.Database;
import com.heliorm.OrmException;
import com.heliorm.OrmTransaction;
import com.heliorm.OrmTransactionDriver;
import com.heliorm.OrmTransactionException;
import com.heliorm.Table;
import com.heliorm.def.Executable;
import com.heliorm.impl.Part;
import com.heliorm.impl.Selector;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
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
public final class SqlOrm implements Orm {


    private final OrmDriver driver;
    private final Map<Class<?>, Table<?>> tables = new ConcurrentHashMap<>();
    private final Selector selector =  new Selector() {
        @Override
        public <O, P extends Part & Executable> List<O> list(P tail) throws OrmException {
            return SqlOrm.this.list(tail);
        }

        @Override
        public <O, P extends Part & Executable> Stream<O> stream(P tail) throws OrmException {
            return SqlOrm.this.stream(tail);
        }

        @Override
        public <O, P extends Part & Executable> Optional<O> optional(P tail) throws OrmException {
            return SqlOrm.this.optional(tail);
        }

        @Override
        public <O, P extends Part & Executable> O one(P tail) throws OrmException {
            return SqlOrm.this.one(tail);
        }
    };

    /**
     * Create an ORM mapper using the supplied driver instance. This is meant to
     * be used with third party drivers.
     *
     * @param driver The driver used to access data.
     */
     SqlOrm(OrmDriver driver) {
        this.driver = driver;
    }

    @Override
    public <O> O create(O pojo) throws OrmException {
        if (pojo == null) {
            throw new OrmException("Attempt to create a null POJO");
        }
        return driver.create(tableFor(pojo), pojo);
    }

    @Override
    public <O> O update(O pojo) throws OrmException {
        if (pojo == null) {
            throw new OrmException("Attempt to update a null POJO");
        }
        return driver.update(tableFor(pojo), pojo);
    }

    @Override
    public <O> void delete(O pojo) throws OrmException {
        if (pojo == null) {
            throw new OrmException("Attempt to delete a null POJO");
        }
        driver.delete(tableFor(pojo), pojo);
    }

    @Override
    public <T extends Table<O>, O> Select<T, O, T, O> select(T table) {
        return new SelectPart<>(null, table, selector());
    }

    @Override
    public OrmTransaction openTransaction() throws OrmException {
        if (!(driver instanceof OrmTransactionDriver)) {
            throw new OrmTransactionException("The ORM driver does not support transactions");
        }
        return ((OrmTransactionDriver) driver).openTransaction();
    }

    @Override
    public void close() {
    }

    @Override
    public <O> Table<O> tableFor(O pojo) throws OrmException {
        return tableFor((Class<O>)pojo.getClass());
    }

    @Override
    public <O> Table<O> tableFor(Class<O> type) throws OrmException {
        if (type == null) {
            throw new OrmException("Attempt to do table lookup for a null class");
        }
        if (tables.isEmpty()) {
            ServiceLoader<Database> svl = ServiceLoader.load(Database.class);
            for (Database database : svl) {
                for (Table<?> table : database.getTables()) {
                    tables.put(table.getObjectClass(), table);
                }
            }
        }
        Table<?> table = tables.get(type);
        if (table == null) {
            throw new OrmException("Cannot find table for pojo of type " + type.getCanonicalName());
        }
        return (Table<O>) table;
    }

    @Override
    public final Selector selector() {
        return selector;
     }

    private <O, P extends Part & Executable> List<O> list(P tail) throws OrmException {
        try (Stream<O> stream = stream(tail)) {
            return stream.collect(Collectors.toList());
        }
    }

    private <O, P extends Part & Executable> Stream<O> stream(P tail) throws OrmException {
        return  driver.stream(tail);
    }

    private <O, P extends Part & Executable> Optional<O> optional(P tail) throws OrmException {
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

    private <O, P extends Part & Executable> O one(P tail) throws OrmException {
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
