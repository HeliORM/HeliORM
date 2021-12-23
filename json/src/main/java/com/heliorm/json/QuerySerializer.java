package com.heliorm.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heliorm.Orm;
import com.heliorm.Table;
import com.heliorm.def.Select;
import com.heliorm.impl.SelectPart;

public final class QuerySerializer {


    private QuerySerializer() {
    }

    private static Gson gson(Orm orm) {
        return  new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapterFactory(new PartTypeAdapterFactory(orm))
                .create();
    }

    /** Create JSON text for the supplier query structure
     *
     * @param query The query structure
     * @return The JSON text
     */
    public static  <T extends Table<O>, O> String toJson(Orm orm, Select<T,O> query)  {
        return gson(orm).toJson(query);
    }

    /** Create a query structure from JSON text
     *
     * @param json The JSON text
     * @return The query structure
     */
    public static <T extends Table<O>, O> Select<T,O> fromJson(Orm orm, String json)  {
        return gson(orm).fromJson(json, SelectPart.class);
    }

}
