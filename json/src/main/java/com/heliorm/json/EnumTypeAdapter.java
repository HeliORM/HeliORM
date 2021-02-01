package com.heliorm.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import static java.lang.String.format;

public class EnumTypeAdapter extends TypeAdapter<Enum> {

    private final Gson gson;

    public EnumTypeAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, Enum value) throws IOException {
        if (value == null) {
            out.nullValue();
        }
        else {
            JsonObject job = new JsonObject();
            job.addProperty("class", value.getClass().getName());
            job.addProperty("value", value.name());
            gson.getAdapter(JsonElement.class).write(out, job);
        }
    }

    @Override
    public Enum read(JsonReader in) throws IOException {
        JsonElement jel = gson.getAdapter(JsonElement.class).read(in);
        if (jel.isJsonNull()) {
            return null;
        }
        JsonObject job = jel.getAsJsonObject();
        String type = job.get("class").getAsString();
        String value = job.get("value").getAsString();
        try {
            return Enum.valueOf((Class<Enum>)Class.forName(type), value);
        } catch (ClassNotFoundException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

}
