package com.ystrazhko.git.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;


public class MapAdapter extends XmlAdapter<MapElements[], Map<Integer, String>> {
    public MapAdapter() {
    }

    @Override
    public MapElements[] marshal(Map<Integer, String> arg0) throws Exception {
        MapElements[] mapElements = new MapElements[arg0.size()];
        int i = 0;
        for (Map.Entry<Integer, String> entry : arg0.entrySet())
            mapElements[i++] = new MapElements(entry.getKey(), entry.getValue());

        return mapElements;
    }

    @Override
    public Map<Integer, String> unmarshal(MapElements[] arg0) throws Exception {
        Map<Integer, String> r = new HashMap<>();
        for (MapElements mapelement : arg0)
            r.put(mapelement.getGroupId(), mapelement.getLocalPath());
        return r;
    }
}