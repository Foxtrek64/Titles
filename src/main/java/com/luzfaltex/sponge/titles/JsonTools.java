package com.luzfaltex.sponge.titles;

import com.google.gson.Gson;

public class JsonTools {

    public static <T> T SerializeObject(String json, Class<T> outputType) {
        Gson gson = new Gson();

        return gson.fromJson(json, outputType);
    }

    public static String DeserializeObject(Object object) {
        Gson gson = new Gson();

        return gson.toJson(object, object.getClass());
    }
}
