package com.lgc.gitlabtool.git.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.util.JSONParser;

@XmlRootElement(name = "groupInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class GroupInfo {

    private String _jsonGroup;

    public GroupInfo() {}

    public GroupInfo(String json) {
        _jsonGroup = json;
    }

    public Group getGroup() {
        return JSONParser.parseToObject(_jsonGroup, Group.class);
    }

    public void setGroupInfo(String json) {
        _jsonGroup = json;
    }

}
