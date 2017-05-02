package com.lgc.solutiontool.git.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "server")
@XmlAccessorType(XmlAccessType.FIELD)
public class Server {
	
	private String name;
	
	public Server() {}
	
	public Server(String name) {
		this.name = name;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
