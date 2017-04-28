package com.lgc.solutiontool.git.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "servers")
@XmlAccessorType(XmlAccessType.FIELD)
public class Servers {
	
	public Servers() {
		servers = new ArrayList<>();
		servers.add(new Server("gitlab.com"));
		servers.add(new Server("gitlab.lgc.com"));
		servers.add(new Server("Other..."));
	}
	
	@XmlElement(name = "server")
	private List<Server> servers = null;
	
	public List<Server> getServers() {
		return servers;
	}

	public void setServers(List<Server> servers) {
		this.servers = servers;
	}
}
