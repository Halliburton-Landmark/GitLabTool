package com.lgc.gitlabtool.git.services;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.reflect.TypeToken;
import com.lgc.gitlabtool.git.connections.HttpResponseHolder;
import com.lgc.gitlabtool.git.connections.RESTConnector;
import com.lgc.gitlabtool.git.connections.token.CurrentUser;
import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.project.nature.projecttype.ProjectType;
import com.lgc.gitlabtool.git.util.JSONParser;
import com.lgc.gitlabtool.git.util.PathUtilities;

public class ProjectServiceImpl implements ProjectService {
    private RESTConnector _connector;

    private static String privateTokenKey;
    private static String privateTokenValue;

    private static final String GROUP_DOESNT_HAVE_PROJECTS_MESSAGE = "The group has no projects.";
    private static final String PREFIX_SUCCESSFUL_LOAD = " group have been successfully loaded";
    private static final String TOTAL_PAGES_COUNT_HEADER = "X-Total-Pages";
    private static final int MAX_PROJECTS_COUNT_ON_THE_PAGE = 100;

    private static final Logger _logger = LogManager.getLogger(ProjectServiceImpl.class);
    private static ProjectTypeService _projectTypeService;
    private static CurrentUser _currentUser = CurrentUser.getInstance();
    private static JGit _git = JGit.getInstance();

    public ProjectServiceImpl(RESTConnector connector, ProjectTypeService projectTypeService) {
        setConnector(connector);
        setProjectTypeService(projectTypeService);
    }

    private void setProjectTypeService(ProjectTypeService projectTypeService) {
        if (projectTypeService != null) {
            _projectTypeService = projectTypeService;
        }

    }

    @Override
    public Collection<Project> getProjects(Group group) {
        Map<String, String> header = getCurrentPrivateToken();
        if (!header.isEmpty()) {
            String sendString = "/groups/" + group.getId() + "/projects?per_page=" + MAX_PROJECTS_COUNT_ON_THE_PAGE;
            return getProjectsForAllPages(sendString, header);
        }
        return Collections.emptyList();
    }

    private Map<String, String> getCurrentPrivateToken() {
        privateTokenValue = _currentUser.getPrivateTokenValue();
        privateTokenKey = _currentUser.getPrivateTokenKey();

        Map<String, String> header = new HashMap<>();
        if (privateTokenValue != null) {
            header.put(privateTokenKey, privateTokenValue);
        }
        return header;
    }

    private Collection<Project> getProjectsForAllPages(String requestString, Map<String, String> header) {
        HttpResponseHolder httpResponse = getConnector().sendGet(requestString, null, header);
        Object jsonProjects = httpResponse.getBody();
        Collection<Project> projects = JSONParser.parseToCollectionObjects(jsonProjects,
                new TypeToken<List<Project>>() {}.getType());

        int countOfPages = getCountOfPages(httpResponse);
        for (int i = 2; i <= countOfPages; i++) {
            String nextPageString = requestString + "&page=" + i;
            Object nextPageJSONProjects = getConnector().sendGet(nextPageString, null, header).getBody();
            Collection<Project> nextPageProjects = JSONParser.parseToCollectionObjects(nextPageJSONProjects,
                    new TypeToken<List<Project>>() {}.getType());
            projects.addAll(nextPageProjects);
        }

        return projects;
    }

    private int getCountOfPages(HttpResponseHolder responseHolder) {
        String header = responseHolder.getHeaderLines().get(TOTAL_PAGES_COUNT_HEADER).get(0);
        return header != null ? Integer.parseInt(header) : 1;
    }

    private RESTConnector getConnector() {
        return _connector;
    }

    private void setConnector(RESTConnector connector) {
        _connector = connector;
    }

    @Override
    public Collection<Project> loadProjects(Group group) {
        Collection<Project> projects = getProjects(group);
        if (projects == null || projects.isEmpty()) {
            _logger.error(GROUP_DOESNT_HAVE_PROJECTS_MESSAGE);
            return Collections.emptyList();
        }
        String successMessage = "The projects of " + group.getName() + PREFIX_SUCCESSFUL_LOAD;
        Path path = Paths.get(group.getPathToClonedGroup());
        Collection<String> projectsName = PathUtilities.getFolders(path);
        if (projectsName.isEmpty()) {
            _logger.debug(successMessage);
            return projects;
        }
        projects.stream()
                .filter(project -> projectsName.contains(project.getName()))
                .forEach((project) -> updateProjectStatus(project, group.getPathToClonedGroup()));
        _logger.debug(successMessage);
        return projects;
    }

    private void updateProjectStatus(Project project, String pathGroup) {
        project.setClonedStatus(true);
        project.setPathToClonedProject(pathGroup + File.separator + project.getName());
        project.setProjectType(_projectTypeService.getProjectType(project));
    }

    private Project createRemoteProject(Group group, String name) {
        Map<String, String> param = new HashMap<>();
        param.put("name", name);
        param.put("namespace_id", String.valueOf(group.getId()));

        Map<String, String> header = getCurrentPrivateToken();
        if(!header.isEmpty()) {
            _logger.info("Creating remote project...");
            Object obj = getConnector().sendPost("/projects", param, header).getBody();
            return JSONParser.parseToObject(obj, Project.class);
        }
        return null;
    }

    private boolean createLocalProject(Project project, String path, String idProjectType) {
        List<Project> projects = Arrays.asList(project);
        _git.clone(projects, path, EmptyProgressListener.get());
        ProjectType projectType = _projectTypeService.getTypeById(idProjectType);
        Set<String> structures = projectType.getStructures();

        List<String> files = structures.stream()
                               .filter(stucture -> PathUtilities.createPath(
                                       Paths.get(project.getPathToClonedProject() + File.separator + stucture)))
                               .collect(Collectors.toList());

        if (files.size() == structures.size()) {
            _logger.info("Structure of type was successfully created!");
            if (structures.size() > 0) {
                _git.addUntrackedFileForCommit(structures, project);
            }
            _git.commitAndPush(projects, "Created new project", true, null, null, null, null,
                    EmptyProgressListener.get());
            return true;
        } else {
            _logger.error("Failed creating structure of type!");
            _git.commitAndPush(projects, "Created new project", true, null, null, null, null,
                    EmptyProgressListener.get());
            PathUtilities.deletePath(Paths.get(project.getPathToClonedProject()));
            return false;
        }
    }

    @Override
    public Project createProject(Group group, String name, String idProjectType) {
        _logger.info("Started creating project in the " + group.getName() + " group.");
        Project project = createRemoteProject(group, name);
        if (project == null) {
            _logger.error("Failed creating remote project!");
            return project;
        } else {
            _logger.info("Remote project was successfully created!");
        }
        _logger.info("Creating local project...");
        boolean result = createLocalProject(project, group.getPathToClonedGroup(), idProjectType);
        if (result) {
            _logger.info("Local project was successfully created!");
        } else {
            _logger.error("Failed creating local project!");
        }
        return project;
    }
}