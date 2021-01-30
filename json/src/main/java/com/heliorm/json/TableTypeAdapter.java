package com.heliorm.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.heliorm.Orm;
import com.heliorm.OrmException;
import com.heliorm.Table;

import java.io.IOException;

import static java.lang.String.format;

public class TableTypeAdapter extends TypeAdapter<Table> {

    private final Gson gson;
    private final Orm orm;

    public TableTypeAdapter(Gson gson, Orm orm) {
        this.gson = gson;
        this.orm = orm;
    }

    @Override
    public void write(JsonWriter out, Table table) throws IOException {
        if (table != null) {
            out.value(table.getObjectClass().getName());
        }
        else {
            out.nullValue();
        }
    }

    @Override
    public Table read(JsonReader in) throws IOException {
        JsonElement jel = gson.getAdapter(JsonElement.class).read(in);
        if (jel.isJsonPrimitive()) {
            try {
                Class type = Class.forName(jel.getAsString());
                return orm.tableFor(type);
            } catch (ClassNotFoundException | OrmException e) {
                throw new IOException(format("Could not find table for type %s (%s)",jel.getAsString(), e.getMessage()),e);
            }
        }
        if (jel.isJsonNull()) {
            return null;
        }
        throw new IOException(format("Cannot parse table from JSON '%s'",jel));
    }
}
