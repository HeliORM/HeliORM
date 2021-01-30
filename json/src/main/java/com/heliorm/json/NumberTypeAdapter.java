package com.heliorm.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import static java.lang.String.format;

public class NumberTypeAdapter extends TypeAdapter<Number> {

    private final Gson gson;

    public NumberTypeAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, Number value) throws IOException {
        if (value == null) {
            out.nullValue();
        }
        else {
            JsonObject job = new JsonObject();
            job.addProperty("class", value.getClass().getSimpleName());
            job.addProperty("value", value);
            gson.getAdapter(JsonElement.class).write(out, job);
        }
    }

    @Override
    public Number read(JsonReader in) throws IOException {
        JsonElement jel = gson.getAdapter(JsonElement.class).read(in);
        if (jel.isJsonNull()) {
            return null;
        }
        JsonObject job = jel.getAsJsonObject();
        String type = job.get("class").getAsString();
        String value = job.get("value").getAsString();
        switch (type) {
            case "Byte": return Byte.parseByte(value);
            case "Short" : return Short.parseShort(value);
            case "Integer" : return Integer.parseInt(value);
            case "Long" : return Long.parseLong(value);
            case "Float" : return Float.parseFloat(value);
            case "Double" : return Double.parseDouble(value);
            default:
                throw new IOException(format("Do not know how to parse number '%s' of type '%s'", value, type));
        }
    }

}
