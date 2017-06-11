package com.lgc.gitlabtool.git.xml;

import javax.xml.bind.annotation.XmlElement;

class XMLElements {

	@XmlElement
	private String _jsonGroup;

	/**
	 * Constructor to create an instance of the object.
	 *
	 * @param value
	 *            path of local group (value for cloned group)
	 */
	XMLElements(String value) {
		if (value != null) {
			_jsonGroup = value;
		}
	}

	/*
	 * JAXB library need a no arg constructor for marshalling
	 */
	private XMLElements() {
	}

	String getJsonGroup() {
		return _jsonGroup;
	}

}