package com.heliorm.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ClassTypeAdapter extends TypeAdapter<Class> {

    private final Gson gson;

    public ClassTypeAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, Class type) throws IOException {
        out.value(type.getName());
    }

    @Override
    public Class read(JsonReader in) throws IOException {
        JsonElement jel = gson.getAdapter(JsonElement.class).read(in);
        if (jel.isJsonPrimitive()) {
            try {
                return Class.forName(jel.getAsString());
            } catch (ClassNotFoundException e) {
                throw new IOException("Dumpster fire");
            }

        }
        throw new IOException("GFYS");
    }

}
