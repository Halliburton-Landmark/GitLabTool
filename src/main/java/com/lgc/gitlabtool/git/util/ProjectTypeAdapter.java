package com.lgc.gitlabtool.git.util;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.lgc.gitlabtool.git.project.nature.projecttype.ProjectType;
import com.lgc.gitlabtool.git.services.ProjectTypeService;
import com.lgc.gitlabtool.git.services.ServiceProvider;

/**
 * The adapter controls which fields ProjectType we writting to json.
 * Defines what interface implementation we use when parsing a json into an object.
 *
 * @author Lyudmila Lyska
 */
public class ProjectTypeAdapter implements JsonDeserializer<ProjectType>, JsonSerializer<ProjectType> {

    private static final ProjectTypeService _typeService = ServiceProvider.getInstance()
            .getService(ProjectTypeService.class);

    @Override
    public ProjectType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement jsonId = jsonObject.get(ProjectType.ID_KEY);
        return jsonId != null ? _typeService.getTypeById(jsonId.getAsString()) : ProjectTypeService.UNKNOWN_TYPE;
    }

    @Override
    public JsonElement serialize(ProjectType src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ProjectType.ID_KEY, src.getId());
        return jsonObject;
    }


}
