package com.heliorm.collection;

import com.heliorm.OrmException;
import com.heliorm.UncaughtOrmException;
import com.heliorm.def.Executable;
import com.heliorm.impl.Part;
import com.heliorm.impl.Selector;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** A list that can lazy-load using the ORM
 *
 * @param <T> The type of data contained in the list
 * @param <P> The type of query part
 */
public class LazyLoadedList<T, P extends Part & Executable> extends AbstractList<T> {

    private boolean loaded = false;
    private List<T> data;
    private final Optional<Selector> selector;
    private final Optional<P> query;

    public LazyLoadedList() {
        super();
        query = Optional.empty();
        selector = Optional.empty();
    }

    public LazyLoadedList(P query, Selector selector) {
        this.query = Optional.of(query);
        this.selector = Optional.of(selector);
    }

    @Override
    public T get(int index) throws UncaughtOrmException {
        ensureLoaded();
        return data.get(index);
    }

    @Override
    public int size() throws UncaughtOrmException {
        ensureLoaded();
        return data.size();
    }

    @Override
    public T set(int index, T element) {
        ensureLoaded();
        return data.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        ensureLoaded();
        data.add(index, element);
    }

    @Override
    public T remove(int index) {
        ensureLoaded();
        return data.remove(index);
    }

    private void ensureLoaded() throws UncaughtOrmException {
        if (!loaded) {
            load();
        }
    }

    private void load() {
        if (query.isPresent()) {
            try {
               data =  selector.get().list(query.get());
            } catch (OrmException e) {
                throw new UncaughtOrmException(e.getMessage(), e);
            }
        }
        else {
            data = new ArrayList();
        }
        loaded = true;
    }

}
