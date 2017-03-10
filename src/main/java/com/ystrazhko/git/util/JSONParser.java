package com.ystrazhko.git.util;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JSONParser {

    private static final Gson _gson = new Gson();
    private static final Type _mapType = new TypeToken<Map<String, Object>>() {}.getType();
    /**
     * Parses from json to map
     *
     * @param json data
     * @return Map<String, Object> or null, if json equals null
     */
    public static Map<String, Object> parseToMap(String json) {
        if (json == null) {
            return null;
        }

        try {
            return _gson.fromJson(json, _mapType);
        } catch(com.google.gson.JsonSyntaxException ex) {
            return null;
        }
    }

    /**
     * Parses from map to json
     *
     * @param data map with data
     * @return json or null, if map equals null
     */
    public static String parseToJson(Map<String, Object> data) {
        if (data != null && data != Collections.EMPTY_MAP) {
            return _gson.toJson(data);
        }
        return null;
    }

    /**
     *
     *
     * @param json
     * @param classObject
     * @return
     */
    public static <T> T parseToObject(Object json, Class<T> classObject) {
        if(classObject == null || json == null) {
            return null;
        }
        try {
            return  _gson.fromJson((String) json, classObject);
        } catch(com.google.gson.JsonSyntaxException ex) {
            return null;
        }
    }

    /**
     *
     * @param json
     * @param typeClass
     * @return
     */
    public static <T> Collection<T> parseToListObjects(Object json, Type typeClass) {
        if(typeClass == null || json == null) {
            return null;
        }
        try {
            return  _gson.fromJson((String) json, typeClass);
        } catch(com.google.gson.JsonSyntaxException ex) {
            return null;
        }
    }

}
