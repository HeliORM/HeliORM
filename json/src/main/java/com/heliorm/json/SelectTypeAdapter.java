package com.heliorm.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.heliorm.impl.Part;

import java.io.IOException;

public class SelectTypeAdapter extends TypeAdapter<Part> {

    private final SelectTypeAdapterFactory factory;
    private final Gson gson;
    private final TypeToken<Part> typeToken;

    public SelectTypeAdapter(SelectTypeAdapterFactory factory, Gson gson, TypeToken<Part> typeToken) {
        this.factory = factory;
        this.gson = gson;
        this.typeToken = typeToken;
    }

    @Override
    public void write(JsonWriter out, Part object) throws IOException {
        if (factory.hasObject(object)) {
            JsonObject job = new JsonObject();
            job.addProperty("serial-ref", factory.getObjectId(object));
            gson.getAdapter(JsonElement.class).write(out, job);
        }
        else {
            JsonElement jel = gson.getDelegateAdapter(factory, typeToken).toJsonTree(object);
            if (jel.isJsonObject()) {
                String ref = factory.saveObject(object);
                JsonObject job = (JsonObject) jel;
                job.addProperty("serial-ref", factory.getObjectId(object));
            }
            gson.getAdapter(JsonElement.class).write(out, jel);
        }
    }

    @Override
    public Part read(JsonReader in) throws IOException {
        JsonElement jel = gson.getAdapter(JsonElement.class).read(in);
        System.out.printf("[%s]\n",jel.toString());
        if (jel.isJsonObject()) {
            return gson.getDelegateAdapter(factory, typeToken).fromJsonTree(jel);
        }
        if (jel.isJsonPrimitive()) {
            String id = jel.getAsString();
            return (Part) factory.getObject(id);
        }
        if (jel.isJsonNull()) {
            return null;
        }
        else {
            throw new IOException("Dog show");
        }
    }
}
