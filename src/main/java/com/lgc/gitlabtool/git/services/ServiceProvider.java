package com.lgc.gitlabtool.git.services;

import java.util.HashMap;
import java.util.Map;

import com.lgc.gitlabtool.git.connections.RESTConnector;
import com.lgc.gitlabtool.git.connections.RESTConnectorFactory;
import com.lgc.gitlabtool.git.jgit.JGit;

public class ServiceProvider {

    private static ServiceProvider _instance;

    private final Map<String, Service> _services;

    public static ServiceProvider getInstance() {
        if (_instance == null) {
            _instance = new ServiceProvider();
        }
        return _instance;
    }

    public Service getService(String serviceName) {
        return _services.get(serviceName);
    }

    private ServiceProvider() {
        JGit jGit = JGit.getInstance();
        BackgroundService backgroundService = new BackgroundServiceImpl();
        RESTConnector restConnector = RESTConnectorFactory.getInstance().getRESTConnector();
        LoginService loginService = new LoginServiceImpl(restConnector, backgroundService);
        StorageService storageService = new StorageServiceImpl();
        ProjectTypeService projectTypeService = new ProjectTypeServiceImpl();
        StateService stateService = new StateServiceImpl();
        GitService gitService = new GitServiceImpl(stateService, jGit);
        ConsoleService consoleService = new ConsoleServiceImpl();
        ProjectService projectService = new ProjectServiceImpl(restConnector, projectTypeService,
                stateService, consoleService, gitService, jGit);
        ClonedGroupsService programPropertiesService = new ClonedGroupsServiceImpl(storageService, loginService);
        PomXmlEditService pomXmlEditService = new PomXMLEditServiceImpl();

        _services = new HashMap<>();
        _services.put(LoginService.class.getName(), loginService);
        _services.put(ClonedGroupsService.class.getName(), programPropertiesService);
        _services.put(GroupsUserService.class.getName(), new GroupsUserServiceImpl(restConnector,
                programPropertiesService, projectService, stateService, consoleService));
        _services.put(ProjectService.class.getName(), projectService);
        _services.put(StorageService.class.getName(), storageService);
        _services.put(ReplacementService.class.getName(), new ReplacementServiceImpl());
        _services.put(PomXMLService.class.getName(), new PomXMLServiceImpl(consoleService, stateService, pomXmlEditService));
        _services.put(ProjectTypeService.class.getName(), projectTypeService);
        _services.put(NetworkService.class.getName(), new NetworkServiceImpl(backgroundService));
        _services.put(GitService.class.getName(), gitService);
        _services.put(StateService.class.getName(), stateService);
        _services.put(ConsoleService.class.getName(), consoleService);
        _services.put(BackgroundService.class.getName(), backgroundService);
    }

    public void stop() {
        if (_services != null) {
            _services.values().forEach(Service::dispose);
        }
    }
}