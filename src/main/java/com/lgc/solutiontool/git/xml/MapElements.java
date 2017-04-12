package com.lgc.solutiontool.git.xml;

import javax.xml.bind.annotation.XmlElement;

/**
 * Additional class for MapAdapter
 * <p>
 * Represents element of map and their behavior
 * Uses only with JAXB library
 *
 * @author Pavlo Pidhornyi
 */
class MapElements {
    @XmlElement
    private Integer groupId;

    @XmlElement
    private String localPath;

    /**
     * Constructor to create an instance of the object.
     *
     * @param key   groupID value (key for cloned group)
     * @param value path of local group (value for cloned group)
     */
    MapElements(Integer key, String value) {
        this.groupId = key;
        this.localPath = value;
    }

    /*
     * JAXB library need a no arg constructor for marshalling
     */
    private MapElements() {
    }

    /**
     * Gets a local path of cloned group
     *
     * @return local path
     */
    String getLocalPath() {
        return localPath;
    }

    /**
     * Gets a groupId of cloned group
     *
     * @return group id
     */
    Integer getGroupId() {
        return groupId;
    }

}