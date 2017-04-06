package com.ystrazhko.git.xml;

import com.ystrazhko.git.entities.Group;
import com.ystrazhko.git.services.GroupsUserService;
import com.ystrazhko.git.services.ServiceProvider;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;


public class MapAdapter extends XmlAdapter<MapElements[], Map<Group, String>> {
    private GroupsUserService _groupsService =
            (GroupsUserService) ServiceProvider.getInstance().getService(GroupsUserService.class.getName());

    public MapAdapter() {
    }

    @Override
    public MapElements[] marshal(Map<Group, String> arg0) throws Exception {
        MapElements[] mapElements = new MapElements[arg0.size()];
        int i = 0;
        for (Map.Entry<Group, String> entry : arg0.entrySet()) {
            mapElements[i++] = new MapElements(entry.getKey().getId(), entry.getValue());
        }
        return mapElements;
    }

    @Override
    public Map<Group, String> unmarshal(MapElements[] arg0) throws Exception {
        Map<Group, String> objMap = new HashMap<>();
        for (MapElements mapelement : arg0) {
            objMap.put( _groupsService.getGroupById(mapelement.getGroupId()), mapelement.getLocalPath());
        }
        return objMap;
    }
}