package com.heliorm.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ClassTypeAdapter extends TypeAdapter<Class> {
    @Override
    public void write(JsonWriter out, Class type) throws IOException {
        out.beginObject();
        out.name("class");
        out.value(type.getName());
        out.endObject();
    }

    @Override
    public Class read(JsonReader in) throws IOException {
        if (in.hasNext()) {
            String name = in.nextName();
            if (name.equals("class")) {
                String value = in.nextString();
                try {
                    return Class.forName(in.nextString());
                } catch (ClassNotFoundException e) {
                    throw new IOException("Dumpster fire");
                }
            }
        }
        throw new IOException("Tits up");
    }
}
