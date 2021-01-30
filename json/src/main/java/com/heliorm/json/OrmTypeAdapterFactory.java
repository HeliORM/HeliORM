package com.heliorm.json;

import com.google.gson.TypeAdapterFactory;
import com.heliorm.Orm;

abstract class OrmTypeAdapterFactory implements TypeAdapterFactory {
    protected final Orm orm;

    public OrmTypeAdapterFactory(Orm orm) {
        this.orm = orm;
    }

}
