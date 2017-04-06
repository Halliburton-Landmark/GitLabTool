package com.lgc.solutiontool.git.xml;

import javax.xml.bind.annotation.XmlElement;


class MapElements {
    @XmlElement
    private Integer groupId;

    @XmlElement
    private String localPath;

    private MapElements() {
    }

    MapElements(Integer key, String value) {
        this.groupId = key;
        this.localPath = value;
    }

    String getLocalPath() {
        return localPath;
    }

    void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    Integer getGroupId() {
        return groupId;
    }

    void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
}