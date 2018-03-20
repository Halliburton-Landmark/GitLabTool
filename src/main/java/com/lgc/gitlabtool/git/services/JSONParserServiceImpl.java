package com.lgc.gitlabtool.git.services;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lgc.gitlabtool.git.project.nature.projecttype.ProjectType;
import com.lgc.gitlabtool.git.util.ProjectTypeAdapter;

public class JSONParserServiceImpl implements JSONParserService {

    private final Logger logger = LogManager.getLogger(JSONParserServiceImpl.class);
    private final GsonBuilder _gsonBuilder;
    private final Gson _gson;
    private final Type _mapType;

    public JSONParserServiceImpl() {
        _gsonBuilder = new GsonBuilder();
        _gsonBuilder.registerTypeAdapter(ProjectType.class, new ProjectTypeAdapter());
        _gson = _gsonBuilder.create();
        _mapType = new TypeToken<Map<String, Object>>(){}.getType();
    }

    @Override
    public Map<String, Object> parseToMap(String json) {
        if (json != null) {
            try {
                return _gson.fromJson(json, _mapType);
            } catch (JsonSyntaxException ex) {
                logger.error("Error parsing from a json to a map: " + ex.getMessage());
            }
        }
        return null;
    }

    @Override
    public String parseMapToJson(Map<String, Object> data) {
        if (data != null) {
            return _gson.toJson(data);
        }
        return StringUtils.EMPTY;
    }

    @Override
    public String parseObjectToJson(Object obj) {
        if (obj != null) {
            return _gson.toJson(obj);
        }
        return null;
    }

    @Override
    public <T> T parseToObject(Object json, Class<T> classObject) {
        if (classObject != null && json != null) {
            try {
                return _gson.fromJson((String) json, classObject);
            } catch (JsonSyntaxException ex) {
                logger.error("Error parsing from a json to a object: " + ex.getMessage());
            }
        }
        return null;
    }

    @Override
    public <T> Collection<T> parseToCollectionObjects(Object json, Type typeClass) {
        if (typeClass != null && json != null) {
            try {
                return _gson.fromJson((String) json, typeClass);
            } catch (JsonSyntaxException ex) {
                logger.error("Error parsing from a json to a collection of objects: " + ex.getMessage());
            }
        }
        return Collections.emptyList();
    }
}
