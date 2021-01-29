package com.heliorm.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.heliorm.impl.*;

import java.io.IOException;

import static java.lang.String.format;

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
        if (object == null) {
            out.nullValue();
        } else if (factory.hasObject(object)) {
            out.value(factory.getObjectId(object));
        } else {
            String ref = factory.saveObject(object);
            JsonElement jel = gson.getDelegateAdapter(factory, typeToken).toJsonTree(object);
            JsonObject job = (JsonObject) jel;
            job.addProperty("serial-ref", factory.getObjectId(object));
            gson.getAdapter(JsonElement.class).write(out, jel);
        }
    }

    @Override
    public Part read(JsonReader in) throws IOException {
        JsonElement jel = gson.getAdapter(JsonElement.class).read(in);
        if (jel.isJsonObject()) {
            JsonObject job = (JsonObject) jel;
            String id = job.get("serial-ref").getAsString();
            factory.saveObject(id, FakePart.TEMP);
            String typeName = job.get("type").getAsString();
            Part part = (Part) gson.getDelegateAdapter(factory, getTypeToke(typeName)).fromJsonTree(jel);
            factory.saveObject(id, part);
            return part;
        }
        if (jel.isJsonPrimitive()) {
            String id = jel.getAsString();
            return (Part) factory.getObject(id);
        }
        if (jel.isJsonNull()) {
            return null;
        } else {
            throw new IOException("Dog show");
        }
    }

    private TypeToken getTypeToke(String typeName) throws IOException {
        Part.Type type = Part.Type.valueOf(typeName);
        Class<?> javaType = null;
        switch (type) {
            case SELECT:
                javaType = SelectPart.class;
                break;
            case WHERE:
            case AND:
            case OR:
                javaType = ContinuationPart.class;
                break;
            case NESTED_AND:
            case NESTED_OR:
                javaType = ExpressionContinuationPart.class;
                break;
            case FIELD:
                javaType = FieldPart.class;
                break;
            case VALUE_EXPRESSION:
                javaType = ValueExpressionPart.class;
                break;
            case LIST_EXPRESSION:
                javaType = ListExpressionPart.class;
                break;
            case IS_EXPRESSION:
                javaType = IsExpressionPart.class;
                break;
            case ON_CLAUSE:
                javaType = OnClausePart.class;
                break;
            case JOIN:
                javaType = JoinPart.class;
                break;
            case ORDER:
                javaType = OrderedPart.class;
                break;
            default:
                throw new IOException(format("Cannot determine how to deserialize data with type '%s'. BUG!", type));
        }
        return TypeToken.get(javaType);
    }

}
