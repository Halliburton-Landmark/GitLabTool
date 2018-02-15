package com.lgc.gitlabtool.git.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "servers")
@XmlAccessorType(XmlAccessType.FIELD)
public class Servers {

    @XmlElement(name = "server")
    private List<Server> servers = null;

    public Servers() {
        servers = new ArrayList<>();
        servers.add(new Server("git.openearth.community", "v3"));
        servers.add(new Server("gitlab.com", "v3"));
        servers.add(new Server("gitlab.lgc.com", "v3"));
    }

    public Servers(List<Server> servers) {
        this.servers = servers;
    }

    public List<Server> getServers() {
        return servers;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }

    public Optional<Server> getServer(String serverName) {
        return servers.stream()
                      .filter(server -> server.getName().equals(serverName))
                      .findAny();
    }
}
