package com.heliorm.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

public class NumberTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (Number.class.isAssignableFrom(type.getRawType())) {
            return (TypeAdapter<T>) new NumberTypeAdapter(gson);
        }
        return null;
    }
}
