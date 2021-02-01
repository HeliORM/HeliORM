package com.heliorm.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.heliorm.Orm;
import com.heliorm.def.Field;
import com.heliorm.impl.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static java.lang.String.format;

public class PartTypeAdapter extends TypeAdapter<Part> {

    private final PartTypeAdapterFactory factory;
    private final Gson gson;
    private final Orm orm;
    private final TypeToken<Part> typeToken;

    public PartTypeAdapter(PartTypeAdapterFactory factory, Gson gson, Orm orm, TypeToken<Part> typeToken) {
        this.factory = factory;
        this.gson = gson;
        this.orm = orm;
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
            String typeName = getTypeName(job);
            TypeToken typeToken = getPartTypeToken(job);
            String id = job.get("serial-ref").getAsString();
            System.out.println("Reading: " + typeToken.getRawType().getSimpleName());
            factory.saveObject(id, new PlaceHolderPart(id));
            Part part = (Part) gson.getDelegateAdapter(factory, typeToken).fromJsonTree(jel);
            factory.saveObject(id, part);
            return replaceFakes(part);
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

    private String getTypeName(JsonObject job) {
        JsonObject tJob = job.get("type").getAsJsonObject();
        return tJob.get("value").getAsString();
    }

    private Part replaceFakes(Part part) throws IOException {
        if (part != null) {
            if (!factory.isPatched(part)) {
                if (canReplace(part.left())) {
                    replace(part, "left", ((PlaceHolderPart) part.left()).getId());
                }
                if (canReplace(part.right())) {
                    replace(part, "right", ((PlaceHolderPart) part.right()).getId());
                }
                replaceFakes(part.left());
                replaceFakes(part.right());
            }
        }
        return part;
    }

    private void replace(Part part, String name, String id) throws IOException {
        try {
            java.lang.reflect.Field field = getField(part.getClass(), name);
            field.setAccessible(true);
            field.set(part, factory.getObject(id));
            if (!PlaceHolderPart.class.isAssignableFrom(part.getClass())) {
                factory.markPatched(part);
            }
        } catch (IllegalAccessException e) {
            throw new IOException(format("Cannot fix part data (%s)", e.getMessage()), e);
        }
    }

    private boolean canReplace(Part part) {
        if (part != null) {
            if (PlaceHolderPart.class.isAssignableFrom(part.getClass())) {
                String id = ((PlaceHolderPart) part).getId();
                Part stored = factory.getObject(id);
                if (!PlaceHolderPart.class.isAssignableFrom(stored.getClass())) {
                    return true;
                }
            }
        }
        return false;
    }

    private java.lang.reflect.Field getField(Class type, String name) throws IOException {
        Class lookAt = type;
        while (Part.class.isAssignableFrom(lookAt)) {
            Optional<java.lang.reflect.Field> first = Arrays.stream(lookAt.getDeclaredFields())
                    .filter(field -> field.getName().equals(name))
                    .findFirst();
            if (first.isPresent()) {
                return first.get();
            }
            lookAt=lookAt.getSuperclass();
        }
        throw new IOException(format("Cannot find field '%s' on class %s", lookAt.getSimpleName()));
    }


    private TypeToken getFieldPartTypeToken(String typeName) throws IOException {
        Field.FieldType type = Field.FieldType.valueOf(typeName);
        switch (type) {
            case BYTE:
                return TypeToken.get(ByteFieldPart.class);
            case SHORT:
                return TypeToken.get(ShortFieldPart.class);
            case INTEGER:
                return TypeToken.get(IntegerFieldPart.class);
            case LONG:
                return TypeToken.get(LongFieldPart.class);
            case FLOAT:
                return TypeToken.get(FloatFieldPart.class);
            case DOUBLE:
                return TypeToken.get(DoubleFieldPart.class);
            case BOOLEAN:
                return TypeToken.get(BooleanFieldPart.class);
            case DATE:
                return TypeToken.get(DateFieldPart.class);
            case INSTANT:
                return TypeToken.get(InstantFieldPart.class);
            case DURATION:
                return TypeToken.get(DurationFieldPart.class);
            case STRING:
                return TypeToken.get(StringFieldPart.class);
            case ENUM:
                return TypeToken.get(EnumFieldPart.class);
            default:
                throw new IOException(format("Unsupported field type '%s'.BUG!", type));
        }

    }

    private TypeToken getValuePartTypeToken(String typeName) throws IOException {
        Field.FieldType type = Field.FieldType.valueOf(typeName);
        switch (type) {
            case BYTE:
                return TypeToken.get(ByteValueExpressionPart.class);
            case SHORT:
                return TypeToken.get(ShortValueExpressionPart.class);
            case INTEGER:
                return TypeToken.get(IntegerValueExpressionPart.class);
            case LONG:
                return TypeToken.get(LongValueExpressionPart.class);
            case FLOAT:
                return TypeToken.get(FloatValueExpressionPart.class);
            case DOUBLE:
                return TypeToken.get(DoubleValueExpressionPart.class);
            case BOOLEAN:
                return TypeToken.get(BooleanValueExpressionPart.class);
            case DATE:
                return TypeToken.get(DateValueExpressionPart.class);
            case INSTANT:
                return TypeToken.get(InstantValueExpressionPart.class);
            case DURATION:
                return TypeToken.get(DurationValueExpressionPart.class);
            case STRING:
                return TypeToken.get(StringValueExpressionPart.class);
            case ENUM:
                return TypeToken.get(EnumValueExpressionPart.class);
            default:
                throw new IOException(format("Unsupported field type '%s'.BUG!", type));
        }

    }

    private TypeToken getPartTypeToken(JsonObject job) throws IOException {
        JsonObject tJob = job.get("type").getAsJsonObject();
        String typeName = tJob.get("value").getAsString();
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
                JsonObject fJob = job.get("fieldType").getAsJsonObject();
                return getFieldPartTypeToken(fJob.get("value").getAsString());
            case VALUE_EXPRESSION:
                JsonObject dJob = job.get("dataType").getAsJsonObject();
                return getValuePartTypeToken(dJob.get("value").getAsString());
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
