package com.heliorm.sql;

import com.heliorm.Orm;
import com.heliorm.OrmException;
import com.heliorm.OrmTransaction;
import com.heliorm.Table;
import com.heliorm.Where;
import com.heliorm.def.Join;
import com.heliorm.def.Select;
import com.heliorm.impl.JoinPart;
import com.heliorm.impl.SelectPart;
import com.heliorm.impl.Selector;
import com.heliorm.json.QuerySerializer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonOrm implements Orm {

    private final Orm orm;

    public JsonOrm(Orm orm) {
        this.orm = orm;
    }

    @Override
    public <O> O create(O pojo) throws OrmException {
        return orm.create(pojo);
    }

    @Override
    public <O> O update(O pojo) throws OrmException {
        return orm.update(pojo);
    }

    @Override
    public <O> void delete(O pojo) throws OrmException {
        orm.delete(pojo);
    }

    @Override
    public <T extends Table<O>, O> Select<T, O> select(T table) {
        return new SelectPart<>(selector(), table);
    }

    @Override
    public <T extends Table<O>, O> Select<T, O> select(T table, Where<T, O> where) {
        return new SelectPart<>(selector(), table, Optional.of(where), Collections.EMPTY_LIST);
    }

    @Override
    public <T extends Table<O>, O> Select<T, O> select(T table, Join<T, O>... joins) {
        return new SelectPart<>(selector(), table, Optional.empty(), Arrays.asList(joins).stream()
                .map(join -> (JoinPart<?,?,?,?>)join).collect(Collectors.toList()));
    }

    @Override
    public <T extends Table<O>, O> Select<T, O> select(T table, Where<T, O> where, Join<T, O>... joins) {
        return new SelectPart<>(selector(), table, Optional.of(where), Arrays.asList(joins).stream()
                .map(join -> (JoinPart<?,?,?,?>)join).collect(Collectors.toList()));
    }

    @Override
    public OrmTransaction openTransaction() throws OrmException {
        return orm.openTransaction();
    }

    @Override
    public void close() {
        orm.close();
    }

    @Override
    public <O> Table<O> tableFor(O pojo) throws OrmException {
        return orm.tableFor(pojo);
    }

    @Override
    public <O> Table<O> tableFor(Class<O> type) throws OrmException {
        return orm.tableFor(type);
    }

    @Override
    public Selector selector() {
        return new Selector() {
            @Override
            public <T extends Table<O>, O> List<O> list(Select<T,O> tail) throws OrmException {
                return orm.selector().list(viaJson(tail));
            }

            @Override
            public <T extends Table<O>, O>  Stream<O> stream(Select<T,O> tail) throws OrmException {
                return orm.selector().stream(viaJson(tail));
            }

            @Override
            public <T extends Table<O>, O>  Optional<O> optional(Select<T,O> tail) throws OrmException {
                return orm.selector().optional(viaJson(tail));
            }

            @Override
            public <T extends Table<O>, O>  O one(Select<T,O> tail) throws OrmException {
                return orm.selector().one(viaJson(tail));
            }
        };
    }

    private <T extends Table<O>, O> Select<T, O> viaJson(Select<T,O> tail) {
        String json = QuerySerializer.toJson(orm, tail);
        return QuerySerializer.fromJson(orm, json);
    }

}
