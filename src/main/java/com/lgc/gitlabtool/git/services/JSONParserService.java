package com.lgc.gitlabtool.git.services;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

/**
 * Service for working with json.
 * It allows to parse json to an object, a map or a collection and vice versa.
 *
 * Note: It has moved from the old JSONParseUtil class.
 *
 * @author Lyudmila Lyska
 */
public interface JSONParserService extends Service {

    /**
     * Parses from json to map
     *
     * @param json data
     * @return Map<String, Object> or null, if json equals null
     */
    Map<String, Object> parseToMap(String json);

    /**
     * Parses from map to json
     *
     * @param data map with data
     * @return json or null, if map equals null
     */
    String parseMapToJson(Map<String, Object> data);

    /**
     * Parses from object to json
     *
     * @param obj object that will be parsed to json
     * @return json or null if invalid data
     */
    String parseObjectToJson(Object obj);

    /**
     * Parses from json to object of T class
     *
     * @param json string of json with data object
     * @param classObject type object
     *
     * @return T object or null, if transferred incorrect data
     */
    <T> T parseToObject(Object json, Class<T> classObject);

    /**
     * Parses from json to collection of object's T class
     *
     * @param json string of json with data objects
     * @param typeClass type of objects collection
     *
     * @return collection of object's T class or null, if transferred incorrect data
     */
    <T> Collection<T> parseToCollectionObjects(Object json, Type typeClass);


}
