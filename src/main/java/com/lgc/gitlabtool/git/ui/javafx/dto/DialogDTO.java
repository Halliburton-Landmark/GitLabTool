package com.lgc.gitlabtool.git.ui.javafx.dto;

/**
 * Data transfer object for <code>LoginDialog</code> window
 * 
 * @author Igor Khlaponin
 *
 */
public class DialogDTO {
	
	/**
	 * User's login
	 */
	private String login;
	
	/**
	 * User's password
	 */
	private String password;
	
	/**
	 * Selected Gitlab server
	 */
	private String serverUrl;

	/**
	 * Selected Gitlab server (without api-suffix)
	 */
	private String shortServerUrl;
	
	public DialogDTO(String login, String password, String serverUrl, String shortServerUrl) {
		this.login = login;
		this.password = password;
		this.serverUrl = serverUrl;
		this.shortServerUrl = shortServerUrl;
	}
	
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getServerURL() {
		return serverUrl;
	}

	public void setServerURL(String comboBoxValue) {
		this.serverUrl = comboBoxValue;
	}

	public String getShortServerURL() { return shortServerUrl; }
	
}
