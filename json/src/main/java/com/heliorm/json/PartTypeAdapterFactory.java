package com.heliorm.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.heliorm.Orm;
import com.heliorm.impl.Part;
import com.heliorm.impl.Selector;

import java.util.*;

import static java.lang.String.format;

public class PartTypeAdapterFactory extends OrmTypeAdapterFactory {

    private final Map<Part, String> objectToId = new HashMap();
    private final Map<String, Part> idToObject = new HashMap();
    private final Set< Part> patched = new HashSet();
    private final SelectorAdapter selectorAdapter;
    private final Map<Class<?>, PartTypeAdapter> partAdapters = new HashMap();

    public PartTypeAdapterFactory(Orm orm) {
        super(orm);
        this.selectorAdapter = new SelectorAdapter(orm);
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        if (Part.class.isAssignableFrom(typeToken.getRawType())) {
            return findPartAdapter(gson, (TypeToken<Part>) typeToken);
        }
        if (Selector.class.isAssignableFrom(typeToken.getRawType())) {
            return (TypeAdapter<T>) selectorAdapter;
        }
        return null;
    }

    String getObjectId(Part object) {
        return objectToId.get(object);
    }

    boolean hasObject(Part object) {
        return objectToId.containsKey(object);
    }

    String saveObject(Part object) {
        return saveObject(UUID.randomUUID().toString(), object);
    }

    String saveObject(String id, Part object) {
        objectToId.put(object, id);
        idToObject.put(id, object);
        return id;
    }

    private <T> TypeAdapter<T> findPartAdapter(Gson gson, TypeToken<Part> typeToken) {
        return (TypeAdapter<T>) partAdapters.computeIfAbsent(typeToken.getRawType(), key -> new PartTypeAdapter(this, gson, orm, typeToken));
    }


    Part getObject(String id) {
        return idToObject.get(id);
    }

     void markPatched(Part part) {
        patched.add(part);
    }

    boolean isPatched(Part part) {
        return patched.contains(part);
    }

}
