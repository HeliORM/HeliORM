package com.heliorm.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

public class EnumTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();
        if (Enum.class.isAssignableFrom(rawType)) {
            return (TypeAdapter<T>) new EnumTypeAdapter(gson);
        }
        return null;
    }

}