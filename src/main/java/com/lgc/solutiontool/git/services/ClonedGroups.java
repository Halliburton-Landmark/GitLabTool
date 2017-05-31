package com.lgc.solutiontool.git.services;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.xml.XMLAdapter;

@XmlRootElement
public class ClonedGroups {

    private static ClonedGroups _instance;

    private List<Group> _groups;

    /**
     * Gets instance's the class
     *
     * @return instance
     */
    public static ClonedGroups getInstance() {
        if (_instance == null) {
            _instance = new ClonedGroups();
        }
        return _instance;
    }

    private ClonedGroups() {
    }

    @XmlJavaTypeAdapter(XMLAdapter.class)
    public void setClonedGroups(List<Group> groups) {
        if (groups != null) {
            _groups = groups;
        }
    }

    /**
     * Gets a list of cloned groups
     *
     * @return list of cloned groups
     */
    public List<Group> getClonedGroups() {
        return _groups;
    }

}