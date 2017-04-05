package com.ystrazhko.git.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by H185176 on 05.04.2017.
 */
public class MapAdapter extends XmlAdapter<MapElements[], Map<Integer, String>> {
    public MapAdapter() {
    }

    public MapElements[] marshal(Map<Integer, String> arg0) throws Exception {
        MapElements[] mapElements = new MapElements[arg0.size()];
        int i = 0;
        for (Map.Entry<Integer, String> entry : arg0.entrySet())
            mapElements[i++] = new MapElements(entry.getKey(), entry.getValue());

        return mapElements;
    }

    public Map<Integer, String> unmarshal(MapElements[] arg0) throws Exception {
        Map<Integer, String> r = new HashMap<Integer, String>();
        for (MapElements mapelement : arg0)
            r.put(mapelement.groupId, mapelement.localPath);
        return r;
    }
}