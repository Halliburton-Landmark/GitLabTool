package com.lgc.gitlabtool.git.services;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.reflect.TypeToken;
import com.lgc.gitlabtool.git.connections.HttpResponseHolder;
import com.lgc.gitlabtool.git.connections.RESTConnector;
import com.lgc.gitlabtool.git.connections.token.CurrentUser;
import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.Project;
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
        privateTokenValue = CurrentUser.getInstance().getPrivateTokenValue();
        privateTokenKey = CurrentUser.getInstance().getPrivateTokenKey();
        if (privateTokenValue != null) {
            String sendString = "/groups/" + group.getId() + "/projects?per_page=" + MAX_PROJECTS_COUNT_ON_THE_PAGE;
            Map<String, String> header = new HashMap<>();
            header.put(privateTokenKey, privateTokenValue);

            return getProjectsForAllPages(sendString, header);
        }
        return Collections.emptyList();
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
}