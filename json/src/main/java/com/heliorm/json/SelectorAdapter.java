package com.heliorm.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.heliorm.Orm;
import com.heliorm.impl.Selector;

import java.io.IOException;

public class SelectorAdapter extends TypeAdapter<Selector> {
    private final Orm orm;
    public SelectorAdapter(Orm orm) {
        this.orm = orm;
    }

    @Override
    public void write(JsonWriter out, Selector selector) throws IOException {
        out.nullValue();
    }

    @Override
    public Selector read(JsonReader in) throws IOException {
        in.nextNull();
        return orm.selector();
    }
}
