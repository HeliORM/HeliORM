package com.heliorm;

public interface Builder<O> {

    <C> Builder<O> with(Field<O,C> field, C value) throws OrmException;

    O build();

}
