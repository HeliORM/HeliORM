package com.heliorm.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heliorm.Orm;
import com.heliorm.def.Executable;
import com.heliorm.impl.Part;

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
     * @param <Q> The query type
     * @return The JSON text
     */
    public static  <Q extends Part & Executable> String toJson(Orm orm, Q query)  {
        return gson(orm).toJson(query.head());
    }

    /** Create a query structure from JSON text
     *
     * @param json The JSON text
     * @param <Q> The query type
     * @return The query structure
     */
    public static <Q extends Part & Executable> Q fromJson(Orm orm, String json)  {
        return (Q) gson(orm).fromJson(json, Part.class);
    }

}
