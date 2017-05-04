package com.lgc.solutiontool.git.properties;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.services.LoginService;
import com.lgc.solutiontool.git.services.ServiceProvider;
import com.lgc.solutiontool.git.services.StorageService;
import com.lgc.solutiontool.git.util.URLManager;
import com.lgc.solutiontool.git.xml.XMLAdapter;

/**
 * Class keeps data about properties.
 *
 * @author Pavlo Pidhorniy
 */
@XmlRootElement
public class ProgramProperties {

	private static ProgramProperties _instance;

	private List<Group> _clonedGroups;

	private final StorageService _storageService = (StorageService) ServiceProvider.getInstance()
			.getService(StorageService.class.getName());

	private final LoginService _loginService = (LoginService) ServiceProvider.getInstance()
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
   * Gets a list of cloned groups
   *
   * @return list of cloned groups
   */
    public List<Group> getClonedGroups() {
        return _clonedGroups;
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
	 * Loads a list with currently cloned groups
	 */
	public List<Group> loadClonedGroups() {
			String username = _loginService.getCurrentUser().getUsername();
			List<Group> groups = _storageService.loadStorage(URLManager.trimServerURL(_loginService.getServerURL()),username);
			setClonedGroups(groups);
			return getClonedGroups();
	}
}