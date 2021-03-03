package com.heliorm.collection;

import com.heliorm.UncaughtOrmException;

import java.util.*;
import java.util.function.Supplier;

/**
 * A list that can lazy-load using the ORM
 *
 * @param <T> The type of data contained in the list
 */
public class LazyLoadedList<T> extends AbstractList<T> {

    private boolean loaded = false;
    private List<T> data;
    private final Optional<Supplier<Collection<T>>> loadFunction;

    /**
     * Construct an empty list with no load function. This is required by the documentation of List
     */
    public LazyLoadedList() {
        loadFunction = Optional.empty();
    }

    public LazyLoadedList(Supplier<Collection<T>> loadFunction) {
        this.loadFunction = Optional.of(loadFunction);
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
        if (loadFunction.isPresent()) {
            data = new ArrayList(loadFunction.get().get());
        } else {
            data = new ArrayList();
        }
        loaded = true;
    }

}
