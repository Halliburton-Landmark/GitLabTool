package com.lgc.gitlabtool.git.util;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lgc.gitlabtool.git.project.nature.projecttype.ProjectType;

public class JSONParser {

    private static final Logger logger = LogManager.getLogger(JSONParser.class);
    private static final GsonBuilder _gsonBuilder = new GsonBuilder();
    private static  Gson _gson;
    private static final Type _mapType = new TypeToken<Map<String, Object>>() {}.getType();

    static {
        _gsonBuilder.registerTypeAdapter(ProjectType.class, new ProjectTypeAdapter());
        _gson = _gsonBuilder.create();
    }

    /**
     * Parses from json to map
     *
     * @param json data
     * @return Map<String, Object> or null, if json equals null
     */
    public static Map<String, Object> parseToMap(String json) {
        if (json != null) {
            try {
                return _gson.fromJson(json, _mapType);
            } catch (JsonSyntaxException ex) {
                logger.error("", ex);
            }
        }
        return null;
    }

    /**
     * Parses from map to json
     *
     * @param data map with data
     * @return json or null, if map equals null
     */
    public static String parseMapToJson(Map<String, Object> data) {
        if (data != null) {
            return _gson.toJson(data);
        }
        return null;
    }

    /**
     * Parses from object to json
     *
     * @param obj object that will be parsed to json
     * @return json or null if invalid data
     */
    public static String parseObjectToJson(Object obj) {
        if (obj != null) {
            return _gson.toJson(obj);
        }
        return null;
    }

    /**
     * Parses from json to object of T class
     *
     * @param json string of json with data object
     * @param classObject type object
     *
     * @return T object or null, if transferred incorrect data
     */
    public static <T> T parseToObject(Object json, Class<T> classObject) {
        if (classObject != null && json != null) {
            try {
                return _gson.fromJson((String) json, classObject);
            } catch (JsonSyntaxException ex) {
                logger.error("", ex);
            }
        }
        return null;
    }

    /**
     * Parses from json to collection of object's T class
     *
     * @param json string of json with data objects
     * @param typeClass type of objects collection
     *
     * @return collection of object's T class or null, if transferred incorrect data
     */
    public static <T> Collection<T> parseToCollectionObjects(Object json, Type typeClass) {
        if (typeClass != null && json != null) {
            try {
                return _gson.fromJson((String) json, typeClass);
            } catch (JsonSyntaxException ex) {
                logger.error("", ex);
            }
        }
        return null;
    }
}
