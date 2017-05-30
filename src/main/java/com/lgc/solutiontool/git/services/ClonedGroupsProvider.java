package com.lgc.solutiontool.git.services;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.xml.XMLAdapter;

@XmlRootElement
public class ClonedGroupsProvider {

    private static ClonedGroupsProvider _instance;

    private List<Group> _clonedGroups;

    /**
     * Gets instance's the class
     *
     * @return instance
     */
    public static ClonedGroupsProvider getInstance() {
        if (_instance == null) {
            _instance = new ClonedGroupsProvider();
        }
        return _instance;
    }

    private ClonedGroupsProvider() {
    }

    @XmlJavaTypeAdapter(XMLAdapter.class)
    public void setClonedGroups(List<Group> groups) {
        if (groups != null) {
            _clonedGroups = groups;
        }
    }

    /**
     * Gets a list of cloned groups
     *
     * @return list of cloned groups
     */
    public List<Group> getClonedGroups() {
        return _clonedGroups;
    }

}