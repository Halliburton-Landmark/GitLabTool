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
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Status;

import com.google.gson.reflect.TypeToken;
import com.lgc.gitlabtool.git.connections.HttpResponseHolder;
import com.lgc.gitlabtool.git.connections.RESTConnector;
import com.lgc.gitlabtool.git.connections.token.CurrentUser;
import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.ProjectStatus;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.project.nature.projecttype.ProjectType;
import com.lgc.gitlabtool.git.ui.javafx.ProgressDialog;
import com.lgc.gitlabtool.git.ui.javafx.listeners.OperationProgressListener;
import com.lgc.gitlabtool.git.util.JSONParser;
import com.lgc.gitlabtool.git.util.PathUtilities;

public class ProjectServiceImpl implements ProjectService {
    private static final String GROUP_DOESNT_HAVE_PROJECTS_MESSAGE = "The group has no projects.";
    private static final String PREFIX_SUCCESSFUL_LOAD = " group have been successfully loaded";
    private static final String TOTAL_PAGES_COUNT_HEADER = "X-Total-Pages";
    private static final int MAX_PROJECTS_COUNT_ON_THE_PAGE = 100;

    private static final String CREATE_LOCAL_PROJECT_SUCCESS_MESSAGE = "Local project was successfully created!";
    private static final String CREATE_LOCAL_PROJECT_FAILED_MESSAGE = "Failed creating local project!";
    private static final String CREATE_REMOTE_PROJECT_SUCCESS_MESSAGE = "Remote project was successfully created!";
    private static final String CREATE_REMOTE_PROJECT_FAILED_MESSAGE = "Failed creating remote project!";
    private static final String CREATE_STRUCTURES_TYPE_SUCCESS_MESSAGE = "Structure of type was successfully created!";
    private static final String CREATE_STRUCTURES_TYPE_FAILED_MESSAGE = "Failed creating structure of type!";
    private static final String PROJECT_ALREADY_EXISTS_MESSAGE = "Project with this name already exists!";

    private static final Logger _logger = LogManager.getLogger(ProjectServiceImpl.class);
    private static final CurrentUser _currentUser = CurrentUser.getInstance();
    private static final JGit _git = JGit.getInstance();

    private static ProjectTypeService _projectTypeService;
    private static StateService _stateService;
    private static RESTConnector _connector;

    public ProjectServiceImpl(RESTConnector connector, ProjectTypeService projectTypeService, StateService stateService) {
        setConnector(connector);
        setProjectTypeService(projectTypeService);
        setStateService(stateService);
    }

    @Override
    public Collection<Project> getProjects(Group group) {
        Map<String, String> header = getCurrentPrivateToken();
        if (!header.isEmpty()) {
            String sendString = "/groups/" + group.getId() + "/projects?per_page=" + MAX_PROJECTS_COUNT_ON_THE_PAGE;
            return getProjectsForAllPages(sendString, header);
        }
        _logger.debug("Error getting projects from the GitLab.");
        return Collections.emptyList();
    }

    @Override
    public Collection<Project> loadProjects(Group group) {
        Collection<Project> projects = getProjects(group);
        if (projects.isEmpty()) {
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

    @Override
    public void createProject(Group group, String name, ProjectType projectType, 
            ProgressListener progressListener, ProgressDialog progressDialog) {
        if(group == null || !group.isCloned() || name == null || name.isEmpty() || projectType == null) {
            throw new IllegalArgumentException("Invalid paramenters");
        }
        boolean isExists = isProjectExists(group, name);
        if (isExists) {
            progressListener.onFinish(null, PROJECT_ALREADY_EXISTS_MESSAGE);
            return;
        }
        _logger.info("Started creating project in the " + group.getName() + " group.");
        Project project = createRemoteProject(group, name, progressListener);
        if (project == null) {
            _logger.error(CREATE_REMOTE_PROJECT_FAILED_MESSAGE);
            progressListener.onFinish(project, CREATE_REMOTE_PROJECT_FAILED_MESSAGE);
            return;
        } else {
            _logger.info(CREATE_REMOTE_PROJECT_SUCCESS_MESSAGE);
        }
        createLocalProject(project, group.getPathToClonedGroup(), projectType, progressListener, progressDialog);
    }

    @Override
    public boolean isProjectExists(Group group, String nameProject) {
        if(group == null || !group.isCloned() || nameProject == null || nameProject.isEmpty()) {
            throw new IllegalArgumentException("Invalid paramenters");
        }
        Collection<Project> projects = getProjects(group);
        Optional<Project> resultProject = projects.stream()
                                                  .filter(project -> project.getName().equals(nameProject))
                                                  .findAny();
        return resultProject.isPresent();
    }

    private Map<String, String> getCurrentPrivateToken() {
        String privateTokenValue = _currentUser.getPrivateTokenValue();
        String privateTokenKey = _currentUser.getPrivateTokenKey();

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

    private void setProjectTypeService(ProjectTypeService projectTypeService) {
        if (projectTypeService != null) {
            _projectTypeService = projectTypeService;
        }
    }

    private void setStateService(StateService stateService) {
        if (stateService != null) {
            _stateService = stateService;
        }
    }

    private void updateProjectStatus(Project project, String pathGroup) {
        project.setClonedStatus(true);
        project.setPathToClonedProject(pathGroup + File.separator + project.getName());
        project.setProjectType(_projectTypeService.getProjectType(project));
        modifyProjectStatusByGit(project);
    }

    private void modifyProjectStatusByGit(Project project) {
        Optional<Status> status = _git.getStatusProject(project);
        if (status.isPresent()) {
            if (status.get().getConflicting().size() > 0) {
                project.setProjectStatus(ProjectStatus.HAS_CONFLICTS);
                return;
            }
            if (status.get().hasUncommittedChanges()) {
                project.setProjectStatus(ProjectStatus.HAS_CHANGES);
            } else {
                project.setProjectStatus(ProjectStatus.DEFAULT);
            }
        }
    }

    private Project createRemoteProject(Group group, String name, ProgressListener progressListener) {
        Map<String, String> param = new HashMap<>();
        param.put("name", name);
        param.put("namespace_id", String.valueOf(group.getId()));

        Map<String, String> header = getCurrentPrivateToken();
        if(!header.isEmpty()) {
            String startCreatingMessage = "Creating remote project...";
            _logger.info(startCreatingMessage);
            progressListener.onStart(startCreatingMessage);
            Object obj = getConnector().sendPost("/projects", param, header).getBody();
            return JSONParser.parseToObject(obj, Project.class);
        }
        return null;
    }

    private void createLocalProject(Project project, String path, ProjectType projectType, 
            ProgressListener progressListener, ProgressDialog progressDialog) {
        List<Project> projects = Arrays.asList(project);
        progressListener.onStart("Cloning of created project");

        clone(projects, path, new OperationProgressListener(progressDialog, ApplicationState.CLONE) {
            @Override
            public void onSuccess(Object... t) {
                Set<String> structures = projectType.getStructures();
                boolean isCreatedStructure = createStructuresType(structures, projectType, project);
                commitAndPushStructuresType(projects, structures, isCreatedStructure, progressListener);
            }
            @Override
            public void onError(Object... t) {
                _logger.error(CREATE_LOCAL_PROJECT_FAILED_MESSAGE);
                progressListener.onFinish((Object)null, "Failed creating the " + project.getName() + " project!");
            }
        });
    }

    // return true if all files was created successfully
    private boolean createStructuresType(Set<String> structures, ProjectType projectType, Project project) {
        long count = structures.stream()
                               .filter(structure -> createStructure(project, structure))
                               .count();
        return count == structures.size();
    }

    private boolean createStructure(Project project, String structure) {
        Path path = Paths.get(project.getPath() + File.separator + structure);
        return PathUtilities.createPath(path, false);
    }

    private void commitAndPushStructuresType(List<Project> projects, Set<String> structures,
                                             boolean isCreatedStructure,
                                             ProgressListener progressListener) {

        String statusCreatedStructureMessage = isCreatedStructure ? CREATE_STRUCTURES_TYPE_SUCCESS_MESSAGE
                                                                  : CREATE_STRUCTURES_TYPE_FAILED_MESSAGE;
        progressListener.onStart(statusCreatedStructureMessage);
        _logger.info(statusCreatedStructureMessage);

        Project createdProject = projects.get(0); // list of projects always has one element
        if (structures.size() > 0 && isCreatedStructure) {
            _git.addUntrackedFileForCommit(structures, createdProject);
        }
        // make first commit to GitLab repository
        _git.commitAndPush(projects, "Created new project", true, null, null, null, null,
                EmptyProgressListener.get());

        if (!isCreatedStructure) {
            PathUtilities.deletePath(Paths.get(createdProject.getPath()));
        }
        progressListener.onSuccess();
        String createLocalProjectMessage = isCreatedStructure ? CREATE_LOCAL_PROJECT_SUCCESS_MESSAGE
                                                              : CREATE_LOCAL_PROJECT_FAILED_MESSAGE;
        _logger.info(createLocalProjectMessage);

        String fineshedMessage = isCreatedStructure
                ? "The " + createdProject.getName() + " project was successfully created!"
                : "Failed creating the " + createdProject.getName() + " project!";
        progressListener.onFinish(isCreatedStructure ? createdProject : null, fineshedMessage);
    }

    @Override
    public void clone(List<Project> projects, String destinationPath, OperationProgressListener progressListener) {
        if (projects == null || destinationPath == null || progressListener == null) {
            throw new IllegalArgumentException("Invalid parameters.");
        }
        // we must call stateOFF for this state in the progressListener.onFinish method
        _stateService.stateON(ApplicationState.CLONE);

        Path path = Paths.get(destinationPath);
        if (!PathUtilities.isExistsAndDirectory(path)) {
            String errorMessage = path.toAbsolutePath() + " path is not exist or it is not a directory.";
            _logger.error(errorMessage);
            progressListener.onError(null, errorMessage);
            progressListener.onFinish(null, false);
            return;
        }
        _git.clone(projects, destinationPath, progressListener);
    }

    @Override
    public boolean hasShadow(List<Project> projects) {
        return projects.stream()
                .filter(proj -> !proj.isCloned())
                .count() > 0;
    }

    @Override
    public boolean hasCloned(List<Project> projects) {
        return projects.stream()
                .filter(Project::isCloned)
                .count() > 0;
    }
}