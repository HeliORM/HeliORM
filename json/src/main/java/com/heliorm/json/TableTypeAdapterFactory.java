package com.heliorm.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.heliorm.Orm;
import com.heliorm.Table;

public final class TableTypeAdapterFactory extends OrmTypeAdapterFactory {
    public TableTypeAdapterFactory(Orm orm) {
        super(orm);
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (Table.class.isAssignableFrom(type.getRawType())) {
            return (TypeAdapter<T>) new TableTypeAdapter(gson, orm);
        }
        return null;
    }
}
