package com.lgc.gitlabtool.git.services;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryBase;
import org.apache.maven.model.Scm;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.JGitStatus;

import javafx.fxml.FXML;

/**
 * Service for changes of a group's pom.xml.
 *
 * @author Lyudmila Lyska
 */
public class PomXMLServiceImpl implements PomXMLService {

    private static final Logger logger = LogManager.getLogger(PomXMLModel.class);

    private static final String RELEASE_NAME_KEY = "releaseName";
    private static final String ECLIPSE_RELEASE_KEY = "eclipse.release";
    private static final String LAYOUT_KEY = "layout";
    private static final String POM_NAME = "pom.xml";
    
    private static final String SUCCESSFUL_CHANGE_MESSAGE = "The pom.xml file was changed successfully.";
    private static final String CHANGE_ERROR_MESSAGE = "ERROR in changing the pom.xml file.";

    private void errorNotValidDataInLog() {
        logger.error("Not valid data was submitted. Cannot modify the pom.xml files.");
    }

    @Override
    public void changeParentVersion(Collection<Project> projects, String newVersion) {
        if (projects == null || !isValidString(newVersion)) {
            errorNotValidDataInLog();
            return;
        }
        for (Project project : projects) {
            if (project == null) {
                continue;
            }
            PomXMLModel model = getModel(project);
            if (changeParentVersion(model, newVersion)) {
                model.writeToFile();
                logger.info(SUCCESSFUL_CHANGE_MESSAGE + " [Project: " + project.getName() + "]");
            } else {
                logger.error(CHANGE_ERROR_MESSAGE + " [Project: " + project.getName() + "]");
            }
        }
    }

    @Override
    public void changeGroupName(Collection<Project> projects, String oldName, String newName) {
        if (projects == null || !isValidString(newName) || !isValidString(oldName)) {
            errorNotValidDataInLog();
            return;
        }
        for (Project project : projects) {
            if (project == null) {
                continue;
            }
            PomXMLModel model = getModel(project);
            if (changeGroupName(model, oldName, newName)) {
                model.writeToFile();
                logger.info(SUCCESSFUL_CHANGE_MESSAGE + " [Project: " + project.getName() + "]");
            } else {
                logger.error(CHANGE_ERROR_MESSAGE + " [Project: " + project.getName() + "]");
            }
        }
    }

    @Override
    public void changeReleaseName(Collection<Project> projects, String newName) {
        if (projects == null || !isValidString(newName)) {
            errorNotValidDataInLog();
            return;
        }
        for (Project project : projects) {
            if (project == null) {
                continue;
            }
            PomXMLModel model = getModel(project);
            if (changeReleaseName(model, newName)) {
                model.writeToFile();
                logger.info(SUCCESSFUL_CHANGE_MESSAGE + " [Project: " + project.getName() + "]");
            } else {
                logger.error(CHANGE_ERROR_MESSAGE + " [Project: " + project.getName() + "]");
            }
        }
    }

    @FXML
    public Set<String> getReposIds(List<Project> projects, Boolean isCommon){
        Set<String> uniqueIds = new HashSet<>();

        if (projects == null ) {
            errorNotValidDataInLog();
            return uniqueIds;
        }

        for (Project project : projects) {
            List<String> projectIds = new ArrayList<>();

            if (project == null) {
                continue;
            }

            PomXMLModel pomModel = getModel(project);
            Model model = pomModel.getModelFile();
            if (model == null) {
                continue;
            }

            projectIds = model.getRepositories().stream()
                    .map(RepositoryBase::getId)
                    .collect(Collectors.toList());

            mergeCollections(uniqueIds, projectIds, isCommon);
        }

        return uniqueIds;
    }

    @Override
    public Map<Project, JGitStatus> addRepository(Collection<Project> projects, String id, String url, String layout) {
        Map<Project, JGitStatus> statuses = new HashMap<>();
        if (projects == null || !isValidString(id) || !isValidString(url)) {
            errorNotValidDataInLog();
            return statuses;
        }
        for (Project project : projects) {
            if (project == null) {
                statuses.put(project, JGitStatus.FAILED);
                continue;
            }
            PomXMLModel pomModel = getModel(project);
            if (addRepository(pomModel, id, url, layout)) {
                pomModel.writeToFile();
                statuses.put(project, JGitStatus.SUCCESSFUL);
                logger.info(SUCCESSFUL_CHANGE_MESSAGE + " [Project: " + project.getName() + "]");
            } else {
                statuses.put(project, JGitStatus.FAILED);
                logger.error(CHANGE_ERROR_MESSAGE + " [Project: " + project.getName() + "]");
            }
        }

        return statuses;
    }

    @Override
    public Map<Project, JGitStatus> removeRepository(Collection<Project> projects, String id) {
        Map<Project, JGitStatus> statuses = new HashMap<>();
        if (projects == null || !isValidString(id)) {
            errorNotValidDataInLog();
            return statuses;
        }
        for (Project project : projects) {
            if (project == null) {
                statuses.put(project, JGitStatus.FAILED);
                continue;
            }
            PomXMLModel pomModel = getModel(project);
            if (removeRepository(pomModel, id)) {
                pomModel.writeToFile();
                statuses.put(project, JGitStatus.SUCCESSFUL);
                logger.info(SUCCESSFUL_CHANGE_MESSAGE + " [Project: " + project.getName() + "]");
            } else {
                statuses.put(project, JGitStatus.FAILED);
                logger.error(CHANGE_ERROR_MESSAGE + " [Project: " + project.getName() + "]");
            }
        }

        return statuses;
    }

    @Override
    public Map<Project, JGitStatus> modifyRepository(Collection<Project> projects, String oldId, String newId, String newUrl, String newLayout) {
        Map<Project, JGitStatus> statuses = new HashMap<>();
        if (projects == null || !isValidString(oldId) || !isValidString(newId) || !isValidString(newUrl)) {
            errorNotValidDataInLog();
            return statuses;
        }
        for (Project project : projects) {
            if (project == null) {
                statuses.put(project, JGitStatus.FAILED);
                continue;
            }
            PomXMLModel pomModel = getModel(project);
            Model model = pomModel.getModelFile();
            if (model == null) {
                statuses.put(project, JGitStatus.FAILED);
                continue;
            }
            List<Repository> rep = model.getRepositories();
            if (modifyRepository(rep, oldId, newId, newUrl, newLayout)) {
                pomModel.writeToFile();
                statuses.put(project, JGitStatus.SUCCESSFUL);
                logger.info(SUCCESSFUL_CHANGE_MESSAGE + " [Project: " + project.getName() + "]");
            } else {
                statuses.put(project, JGitStatus.FAILED);
                logger.error(CHANGE_ERROR_MESSAGE + " [Project: " + project.getName() + "]");
            }
        }

        return statuses;
    }

    @Override
    public String getReleaseName(Collection<Project> projects) {
        if (projects == null) {
            errorNotValidDataInLog();
            return "";
        }

        List<String> names = new ArrayList<>();

        for (Project project : projects) {
            if (project == null) {
                continue;
            }
            PomXMLModel pomModel = getModel(project);
            Model model = pomModel.getModelFile();

            if (model == null) {
                continue;
            }

            names.add(model.getProperties().getProperty(RELEASE_NAME_KEY));
        }

        boolean allEqual = new HashSet<>(names).size() == 1;

        if (allEqual) {
            return names.get(0);
        } else {
            return "[Different]";
        }
    }

    @Override
    public String getEclipseRelease(Collection<Project> projects) {
        if (projects == null) {
            errorNotValidDataInLog();
            return "";
        }

        List<String> eclipseReleases = new ArrayList<>();

        for (Project project : projects) {
            if (project == null) {
                continue;
            }
            PomXMLModel pomModel = getModel(project);
            Model model = pomModel.getModelFile();

            if (model == null) {
                continue;
            }

            eclipseReleases.add(model.getProperties().getProperty(ECLIPSE_RELEASE_KEY));
        }

        boolean allEqual = new HashSet<>(eclipseReleases).size() == 1;

        if (allEqual) {
            return eclipseReleases.get(0);
        } else {
            return "[Different]";
        }
    }

    @Override
    public String getLayout(List<Project> projects, String idRepo) {
        if (projects == null) {
            errorNotValidDataInLog();
            return "";
        }

        List<String> projectsLayouts = new ArrayList<>();

        for (Project project : projects) {
            if (project == null) {
                continue;
            }
            PomXMLModel pomModel = getModel(project);
            Model model = pomModel.getModelFile();

            if (model == null) {
                continue;
            }

            List<Repository> repos = model.getRepositories()
                    .stream()
                    .filter(repo -> repo.getId().equals(idRepo))
                    .collect(Collectors.toList());

            if (repos.size() == 1) {
                projectsLayouts.add(repos.get(0).getLayout());
            } else {
                projectsLayouts.add("[Different]");
            }

        }

        boolean allEqual = new HashSet<>(projectsLayouts).size() == 1;

        if (allEqual) {
            return projectsLayouts.get(0);
        } else {
            return "[Different]";
        }

    }

    @Override
    public String getUrl(List<Project> projects, String idRepo) {
        if (projects == null) {
            errorNotValidDataInLog();
            return "";
        }

        List<String> projectsUrls = new ArrayList<>();

        for (Project project : projects) {
            if (project == null) {
                continue;
            }
            PomXMLModel pomModel = getModel(project);
            Model model = pomModel.getModelFile();

            if (model == null) {
                continue;
            }

            List<Repository> repos = model.getRepositories()
                    .stream()
                    .filter(repo -> repo.getId().equals(idRepo))
                    .collect(Collectors.toList());

            if (repos.size() == 1) {
                projectsUrls.add(repos.get(0).getUrl());
            } else {
                projectsUrls.add("[Different]");
            }

        }

        boolean allEqual = new HashSet<>(projectsUrls).size() == 1;

        if (allEqual) {
            return projectsUrls.get(0);
        } else {
            return "[Different]";
        }
    }

    @Override
    public boolean containsRepository(Project project, String idRepo) {
        if (project == null || idRepo == null) {
            errorNotValidDataInLog();
            return false;
        }

        PomXMLModel pomModel = getModel(project);
        Model model = pomModel.getModelFile();

        return model != null && model.getRepositories()
                .stream()
                .anyMatch(repo -> repo.getId().equals(idRepo));

    }

    private <T> void mergeCollections(Collection<T> first, Collection<T> second, boolean onlyGeneral) {
        if (onlyGeneral && !first.isEmpty()) {
            first.retainAll(second);
        } else {
            first.addAll(second);
        }
    }

    private boolean changeParentVersion(PomXMLModel pomMng, String newVersion) {
        Model model = pomMng.getModelFile();
        if (model != null) {
            Parent parent = model.getParent();
            if (parent != null) {
                parent.setVersion(newVersion);
                return true;
            }
        }
        return false;
    }

    private boolean changeGroupName(PomXMLModel pomMng, String oldName, String newName) {
        Model model = pomMng.getModelFile();
        if (model == null) {
            return false;
        }
        boolean properties = changeGroupNameInProperties(model.getProperties(), oldName, newName);
        boolean repositories = changeGroupNameInRepositories(model.getRepositories(), oldName, newName);
        boolean scm = changeGroupNameInScm(model.getScm(), oldName, newName);
        return properties | repositories | scm;
    }

    private boolean changeReleaseName(PomXMLModel pomMng, String newName) {
        Model model = pomMng.getModelFile();
        if (model != null) {
            Properties pr = model.getProperties();
            if (pr != null) {
                pr.put(RELEASE_NAME_KEY, newName);
                return true;
            }
        }
        return false;
    }

    private boolean changeGroupNameInRepositories(List<Repository> reps, String oldName, String newName) {
        if (reps == null) {
            return false;
        }
        String regex = "(?i)(" + oldName + ")([\\-\\/])(?<=.)";
        Pattern replace = Pattern.compile(regex);

        boolean isChanged = false;
        for (Repository repository : reps) {

            String id = repository.getId();
            if (isCompliteValue(replace, id)) {
                repository.setId(replace.matcher(id).replaceAll(newName + "$2"));
                isChanged = true;
            }

            String url = repository.getUrl();
            if (isCompliteValue(replace, url)) {
                repository.setUrl(replace.matcher(url).replaceAll(newName + "$2"));
                isChanged = true;
            }
        }
        return isChanged;
    }

    private boolean changeGroupNameInProperties(Properties pr, String oldName, String newName) {
        if (pr == null) {
            return false;
        }
        String regex = "(?i)(" + oldName + ")([\\-\\/])(?<=.)";
        Pattern replace = Pattern.compile(regex);

        boolean isChanged = false;
        for (Entry<Object, Object> property : pr.entrySet()) {
            String value = (String) property.getValue();
            if (isCompliteValue(replace, value)) {
                pr.put(property.getKey(), replace.matcher(value).replaceAll(newName + "$2"));
                isChanged = true;
            }
        }
        return isChanged;
    }

    private boolean changeGroupNameInScm(Scm scm, String oldName, String newName) {
        if (scm == null) {
            return false;
        }
        String regex = "(?i)(" + oldName + ")([\\-\\/])(?<=.)";
        Pattern replace = Pattern.compile(regex);

        boolean isChanged = false;
        String connection = scm.getConnection();
        if (isCompliteValue(replace, connection)) {
            scm.setConnection(replace.matcher(connection).replaceAll(newName + "$2"));
            isChanged = true;
        }

        String devConnection = scm.getDeveloperConnection();
        if (isCompliteValue(replace, devConnection)) {
            scm.setDeveloperConnection(replace.matcher(devConnection).replaceAll(newName + "$2"));
            isChanged = true;
        }
        return isChanged;
    }

    private boolean addRepository(PomXMLModel pomMng, String id, String url, String layout) {
        Model model = pomMng.getModelFile();
        if (model == null) {
            return false;
        }

        List<Repository> reps = model.getRepositories();
        Repository repository = new Repository();
        repository.setId(id);
        repository.setUrl(url);
        repository.setLayout(layout);

        if (!isListHasRepository(reps, repository)) {
            reps.add(repository);
            return true;
        }
        return false;
    }

    private boolean modifyRepository(List<Repository> reps, String oldId, String newId, String url, String layout) {
        if (reps == null) {
            return false;
        }
        boolean isChanged = false;
        for (Repository repository : reps) {
            if (repository != null && repository.getId().equals(oldId)) {
                repository.setId(newId);
                repository.setUrl(url);
                repository.setLayout(layout);
                isChanged = true;
            }
        }
        return isChanged;
    }

    private boolean removeRepository(PomXMLModel pomMng, String id) {
        Model model = pomMng.getModelFile();
        if (model == null) {
            return false;
        }
        List<Repository> reps = model.getRepositories();
        Repository remRep = null;
        for (Repository repository : reps) {
            if (id.equals(repository.getId())) {
                remRep = repository;
            }
        }
        if (remRep != null) {
            reps.remove(remRep);
            return true;
        }
        return false;
    }

    private boolean isCompliteValue(Pattern replace, String param) {
        if (param == null) {
            return false;
        }
        return replace.matcher(param).find();
    }

    private boolean isListHasRepository(List<Repository> reps, Repository repository) {
        if (reps == null) {
            return false;
        }
        for (Repository rep : reps) {
            if (rep.getId().equals(repository.getId())) {
                return true;
            }
        }
        return false;
    }

    private PomXMLModel getModel(Project project) {
        String pathToPomXML = findPathToPomXMLFile(project);
        return new PomXMLModel(pathToPomXML);
    }

    private boolean isValidString(String value) {
        return value != null && !value.isEmpty();
    }

    private String findPathToPomXMLFile(Project pr) {
        String pathToProject = pr.getPath();
        if (pathToProject == null) {
            return null;
        }
        Path projectPath = Paths.get(pr.getPath());
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(projectPath)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file) && file.getName(file.getNameCount()-1).toString().equals(POM_NAME)) {
                    return file.toString();
                }
            }
        } catch (IOException e) {
            logger.error("", e);
        }
        return null;
    }
}
