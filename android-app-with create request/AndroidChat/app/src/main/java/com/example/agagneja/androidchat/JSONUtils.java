package com.example.agagneja.androidchat;

import com.google.gson.Gson;

/**
 * Created by agagneja on 1/28/2015.
 */
public class JSONUtils {

    private static final Gson gsonConverter = new Gson();


    public static <T> T convertJson(String jsonString, Class<T> arg) {
        T t = gsonConverter.fromJson(jsonString, arg);
        return arg.cast(t);
    }

    public static String convertToJsonString(Object input) {
        return gsonConverter.toJson(input);
    }
}
