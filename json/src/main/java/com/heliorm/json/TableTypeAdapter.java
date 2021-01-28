package com.heliorm.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.heliorm.Orm;
import com.heliorm.OrmException;
import com.heliorm.Table;

import java.io.IOException;

public class TableTypeAdapter extends TypeAdapter<Table> {

    private final Orm orm;

    public TableTypeAdapter(Orm orm) {
        this.orm = orm;
    }

    @Override
    public void write(JsonWriter out, Table table) throws IOException {
        out.value(table.getObjectClass().getName());
    }

    @Override
    public Table read(JsonReader in) throws IOException {
        String value = in.nextString();
        try {
            Class type = Class.forName(value);
            return orm.tableFor(type);
        } catch (ClassNotFoundException | OrmException e) {
            throw new IOException("Dumpster fire");
        }
    }
}
