package com.lgc.solutiontool.git.properties;

import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.services.LoginService;
import com.lgc.solutiontool.git.services.ServiceProvider;
import com.lgc.solutiontool.git.services.StorageService;
import com.lgc.solutiontool.git.util.URLManager;
import com.lgc.solutiontool.git.xml.XMLAdapter;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.List;

/**
 * Class keeps data about properties.
 *
 * @author Pavlo Pidhorniy
 */
@XmlRootElement
public class ProgramProperties {

	private static ProgramProperties _instance;

	private List<Group> _clonedGroups;

	private StorageService _storageService = (StorageService) ServiceProvider.getInstance()
			.getService(StorageService.class.getName());

	private LoginService _loginService = (LoginService) ServiceProvider.getInstance()
			.getService(LoginService.class.getName());

	/**
	 * Gets instance's the class
	 *
	 * @return instance
	 */
	public static ProgramProperties getInstance() {
		if (_instance == null) {
			_instance = new ProgramProperties();
		}
		return _instance;
	}

	private ProgramProperties() {}

	@XmlJavaTypeAdapter(XMLAdapter.class)
	public void setClonedGroups(List<Group> groups) {
		if (groups != null) {
			_clonedGroups = groups;
		}
	}

	/**
	 * Updates local storage using the current properties
	 */
	public void updateClonedGroups(List<Group> groups) {
		if (groups == null || groups.isEmpty()) {
			return;
		}

		if (_clonedGroups == null || _clonedGroups.isEmpty()) {
			setClonedGroups(groups);
		} else {
			_clonedGroups.addAll(groups);
		}

		String username = _loginService.getCurrentUser().getUsername();
		_storageService.updateStorage(URLManager.trimServerURL(_loginService.getServerURL()), username);
	}

	/**
	 * Gets a list with currently cloned groups
	 *
	 * @return list with groups
	 */
	public List<Group> getClonedGroups() {
		if (_clonedGroups == null) {
			String username = _loginService.getCurrentUser().getUsername();
			List<Group> groups = _storageService.loadStorage(URLManager.trimServerURL(_loginService.getServerURL()),
					username);
			setClonedGroups(groups);
		}
		return _clonedGroups;
	}

}
