package com.heliorm.collection;

import com.heliorm.OrmException;
import com.heliorm.UncaughtOrmException;
import com.heliorm.def.Executable;
import com.heliorm.impl.Part;
import com.heliorm.impl.Selector;

import java.util.*;

/** A set that can lazy-load using the ORM
 *
 * @param <T> The type of data contained in the set
 * @param <P> The type of query part
 */
public class LazyLoadedSet<T, P extends Part & Executable> extends AbstractSet<T> {

    private boolean loaded = false;
    private Set<T> data;
    private final Optional<Selector> selector;
    private final Optional<P> query;

    public LazyLoadedSet() {
        super();
        query = Optional.empty();
        selector = Optional.empty();
    }

    public LazyLoadedSet(P query, Selector selector) {
        this.query = Optional.of(query);
        this.selector = Optional.of(selector);
    }

    @Override
    public Iterator<T> iterator() {
        ensureLoaded();
        return data.iterator();
    }

    @Override
    public int size() {
        ensureLoaded();
        return data.size();
    }

    private void ensureLoaded() throws UncaughtOrmException {
        if (!loaded) {
            load();
        }
    }

    private void load() {
        if (query.isPresent()) {
            try {
               data =  new HashSet(selector.get().list(query.get()));
            } catch (OrmException e) {
                throw new UncaughtOrmException(e.getMessage(), e);
            }
        }
        else {
            data = new HashSet<>();
        }
        loaded = true;
    }

}
