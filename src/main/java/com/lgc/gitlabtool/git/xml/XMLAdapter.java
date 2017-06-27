package com.lgc.gitlabtool.git.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.util.JSONParser;

public class XMLAdapter extends XmlAdapter<XMLElements[], List<Group>> {

    @Override
    public XMLElements[] marshal(List<Group> groups) throws Exception {
        XMLElements[] mapElements = new XMLElements[groups.size()];
        int i = 0;
        for (Group group : groups) {
            String jsonGroup = JSONParser.parseObjectToJson(group);
            if (jsonGroup != null) {
                mapElements[i++] = new XMLElements(jsonGroup);
            }
        }
        return mapElements;
    }

    @Override
    public List<Group> unmarshal(XMLElements[] elements) throws Exception {
        List<Group> groups = new ArrayList<>();
        for (XMLElements element : elements) {
            Group group = JSONParser.parseToObject(element.getJsonGroup(), Group.class);
            if (group != null) {
                groups.add(group);
            }
        }
        return groups;
    }
}