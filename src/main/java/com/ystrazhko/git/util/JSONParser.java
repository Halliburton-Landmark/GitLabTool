package com.ystrazhko.git.util;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JSONParser {

    /**
     * Parses from json to map
     *
     * @param json data
     * @return Map<String, Object> or null, if json equals null
     */
    public static Map<String, Object> parseToMap(String json) {
        if (json != null) {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            return new Gson().fromJson(json, mapType);
        }
        return null;

    }

    /**
     * Parses from map to json
     *
     * @param data map with data
     * @return json or null, if map equals null
     */
    public static String parseToJson(Map<String, Object> data) {
        if (data != null && data != Collections.EMPTY_MAP) {
            return new Gson().toJson(data);
        }
        return null;
    }
}
