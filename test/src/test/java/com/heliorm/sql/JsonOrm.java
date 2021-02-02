package com.heliorm.sql;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heliorm.Orm;
import com.heliorm.OrmException;
import com.heliorm.OrmTransaction;
import com.heliorm.Table;
import com.heliorm.def.Executable;
import com.heliorm.def.Select;
import com.heliorm.impl.Part;
import com.heliorm.impl.SelectPart;
import com.heliorm.impl.Selector;
import com.heliorm.json.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class JsonOrm implements Orm {

    private final Gson serialiser;
    private final Gson deSerialiser;
    private final Orm orm;

    public JsonOrm(Orm orm) {
        this.orm = orm;
        serialiser = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapterFactory(new PartTypeAdapterFactory(orm))
                .create();
        deSerialiser = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapterFactory(new PartTypeAdapterFactory(orm))
                .create();
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
    public <T extends Table<O>, O> Select<T, O, T, O> select(T table) {
        return new SelectPart<>(null, table, selector());
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
            public <O, P extends Part & Executable> List<O> list(P tail) throws OrmException {
                return orm.selector().list(viaJson(tail));
            }

            @Override
            public <O, P extends Part & Executable> Stream<O> stream(P tail) throws OrmException {
                return orm.selector().stream(viaJson(tail));
            }

            @Override
            public <O, P extends Part & Executable> Optional<O> optional(P tail) throws OrmException {
                return orm.selector().optional(viaJson(tail));
            }

            @Override
            public <O, P extends Part & Executable> O one(P tail) throws OrmException {
                return orm.selector().one(viaJson(tail));
            }
        };
    }

    private <O, P extends Part & Executable> P viaJson(P tail) {
        String json = serialiser.toJson(tail.head());
        System.out.println(json);
        P res = (P) deSerialiser.fromJson(json, SelectPart.class);
        return res;
    }

}
