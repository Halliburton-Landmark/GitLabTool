package com.ystrazhko.git.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by H185176 on 05.04.2017.
 */
public class MapElements {
    @XmlElement
    public Integer groupId;
    @XmlElement
    public String localPath;

    private MapElements() {
    }

    public MapElements(Integer key, String value) {
        this.groupId = key;
        this.localPath = value;
    }
}