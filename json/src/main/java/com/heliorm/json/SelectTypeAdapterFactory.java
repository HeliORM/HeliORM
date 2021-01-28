package com.heliorm.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.heliorm.Orm;
import com.heliorm.Table;
import com.heliorm.impl.Part;
import com.heliorm.impl.Selector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SelectTypeAdapterFactory implements TypeAdapterFactory {

    private final Map<Object, String> objectToId = new HashMap();
    private final Map<String, Part> idToObject = new HashMap();
    private final ClassTypeAdapter classAdapter = new ClassTypeAdapter();
    private final TableTypeAdapter tableAdapter;
    private final SelectorAdapter selectorAdapter;
   private final Orm orm;
    public SelectTypeAdapterFactory(Orm orm) {
        this.orm = orm;
        this.tableAdapter = new TableTypeAdapter(orm);
        this.selectorAdapter = new SelectorAdapter(orm);
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        System.out.printf("create %s\n", typeToken.getRawType().getSimpleName());
        if (Part.class.isAssignableFrom(typeToken.getRawType())) {
            return findTypeAdapter(gson, (TypeToken<Part>) typeToken);
        }
        if (Class.class.isAssignableFrom(typeToken.getRawType())) {
            return (TypeAdapter<T>) classAdapter;
        }
        if (Table.class.isAssignableFrom(typeToken.getRawType())) {
            return (TypeAdapter<T>) tableAdapter;
        }
        if (Selector.class.isAssignableFrom(typeToken.getRawType())) {
            return (TypeAdapter<T>) selectorAdapter;
        }
        return null;
    }

    String getObjectId(Object object) {
        return objectToId.get(object);
    }

    boolean hasObject(Object object) {
        return objectToId.containsKey(object);
    }
     String saveObject(Part object) {
        String id = UUID.randomUUID().toString();
        objectToId.put(object, id);
        idToObject.put(id, object);
        return id;
    }


    private <T> TypeAdapter<T>  findTypeAdapter(Gson gson, TypeToken<Part> typeToken) {
        return (TypeAdapter<T>) new SelectTypeAdapter(this, gson, typeToken);
    }


     Object getObject(String id) {
        return idToObject.get(id);
    }
}
