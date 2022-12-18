package com.heliorm.collection;

import com.heliorm.UncaughtOrmException;
import com.heliorm.def.Executable;

import java.util.*;
import java.util.function.Supplier;

/**
 * A set that can lazy-load using the ORM
 *
 * @param <T> The type of data contained in the set
 * @param <P> The type of query part
 */
public class LazyLoadedSet<T, P extends Executable> extends AbstractSet<T> {

    private boolean loaded = false;
    private Set<T> data;
    private final Optional<Supplier<Collection<T>>> loadFunction;

    public LazyLoadedSet() {
        super();
        loadFunction = Optional.empty();
    }

    public LazyLoadedSet(Supplier<Collection<T>> loadFunction) {
        this.loadFunction = Optional.of(loadFunction);
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
        if (loadFunction.isPresent()) {
            data = new HashSet(loadFunction.get().get());
        } else {
            data = new HashSet<>();
        }
        loaded = true;
    }

}
