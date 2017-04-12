package com.lgc.solutiontool.git.services;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Repository;
import org.apache.maven.model.Scm;

/**
 * Service for changes of a group's pom.xml.
 *
 * @author Lyudmila Lyska
 */
public class PomXMLServiceImpl implements PomXMLService {

    private static final String RELEASE_NAME_KEY = "releaseName";
    private static final String REPOSITORY_LAYOUT = "p2";

    @Override
    public void changeParentVersion(Collection<PomXMLManager> pomMngs, String newVersion) {
        if (pomMngs == null || !isValidString(newVersion)) {
            return;
        }
        for (PomXMLManager pomXMLManager : pomMngs) {
            if (pomXMLManager != null) {
                changeParentVersion(pomXMLManager, newVersion);
            }
        }
    }

    @Override
    public void changeGroupName(Collection<PomXMLManager> pomMngs, String oldName, String newName) {
        if (pomMngs == null) {
            return;
        }
        if (!isValidString(newName) || !isValidString(oldName)) {
            return;
        }
        for (PomXMLManager pomXMLManager : pomMngs) {
            if (pomXMLManager != null) {
                changeGroupName(pomXMLManager, oldName, newName);
            }
        }
    }

    @Override
    public void changeReleaseName(Collection<PomXMLManager> pomMngs, String newName) {
        if (pomMngs == null || !isValidString(newName)) {
            return;
        }
        for (PomXMLManager pomXMLManager : pomMngs) {
            if (pomXMLManager != null) {
                changeReleaseName(pomXMLManager, newName);
            }
        }
    }

    @Override
    public void addRepository(Collection<PomXMLManager> pomMngs, String id, String url) {
        if (pomMngs == null) {
            return;
        }
        if (!isValidString(id) || !isValidString(url)) {
            return;
        }

        for (PomXMLManager pomXMLManager : pomMngs) {
            if (pomXMLManager != null) {
                addRepository(pomXMLManager, id, url);
            }
        }
    }

    @Override
    public void removeRepository(Collection<PomXMLManager> pomMngs, String id) {
        if (pomMngs == null || !isValidString(id)) {
            return;
        }
        for (PomXMLManager pomXMLManager : pomMngs) {
            if (pomXMLManager != null) {
                removeRepository(pomXMLManager, id);
            }
        }
    }

    @Override
    public void modifyRepository(Collection<PomXMLManager> pomMngs, String oldId, String newId, String newUrl) {
        if (pomMngs == null) {
            return;
        }
        if (!isValidString(oldId) || !isValidString(newId) || !isValidString(newUrl)) {
            return;
        }
        for (PomXMLManager pomXMLManager : pomMngs) {
            if (pomXMLManager != null) {
                Optional<Model> model = pomXMLManager.getModelFile();
                if (!model.isPresent()) {
                    continue;
                }
                modifyRepository(model.get().getRepositories(), oldId, newId, newUrl);
            }
        }
    }

    private void changeParentVersion(PomXMLManager pomMng, String newVersion) {
        Optional<Model> model = pomMng.getModelFile();
        if (!model.isPresent()) {
            return;
        }
        Parent parent = model.get().getParent();
        if (parent != null) {
            parent.setVersion(newVersion);
        }
    }

    private void changeGroupName(PomXMLManager pomMng, String oldName, String newName) {
        Optional<Model> optModel = pomMng.getModelFile();
        if (!optModel.isPresent()) {
            return;
        }
        Model xmlModel = optModel.get();
        changeGroupNameInProperties(xmlModel.getProperties(), oldName, newName);
        changeGroupNameInRepositories(xmlModel.getRepositories(), oldName, newName);
        changeGroupNameInScm(xmlModel.getScm(), oldName, newName);
    }

    private void changeReleaseName(PomXMLManager pomMng, String newName) {
        Optional<Model> optModel = pomMng.getModelFile();
        if (!optModel.isPresent()) {
            return;
        }
        Properties pr = optModel.get().getProperties();
        if (pr != null) {
            pr.put(RELEASE_NAME_KEY, newName);
        }
    }

    private void changeGroupNameInRepositories(List<Repository> reps, String oldName, String newName) {
        if (reps == null) {
            return;
        }
        String regex = "(?i)(" + oldName + ")([\\-\\/])(?<=.)";
        Pattern replace = Pattern.compile(regex);

        for (Repository repository : reps) {

            String id = repository.getId();
            if (isCompliteValue(replace, id)) {
                repository.setId(replace.matcher(id).replaceAll(newName + "$2"));
            }

            String url = repository.getUrl();
            if (isCompliteValue(replace, url)) {
                repository.setUrl(replace.matcher(url).replaceAll(newName + "$2"));
            }
        }
    }

    private void changeGroupNameInProperties(Properties pr, String oldName, String newName) {
        if (pr == null) {
            return;
        }
        String regex = "(?i)(" + oldName + ")([\\-\\/])(?<=.)";
        Pattern replace = Pattern.compile(regex);

        for (Entry<Object, Object> property : pr.entrySet()) {
            String value = (String) property.getValue();
            if (isCompliteValue(replace, value)) {
                pr.put(property.getKey(), replace.matcher(value).replaceAll(newName + "$2"));
            }
        }
    }

    private void changeGroupNameInScm(Scm scm, String oldName, String newName) {
        if (scm == null) {
            return;
        }
        String regex = "(?i)(" + oldName + ")([\\-\\/])(?<=.)";
        Pattern replace = Pattern.compile(regex);

        String connection = scm.getConnection();
        if (isCompliteValue(replace, connection)) {
            scm.setConnection(replace.matcher(connection).replaceAll(newName + "$2"));
        }

        String devConnection = scm.getDeveloperConnection();
        if (isCompliteValue(replace, devConnection)) {
            scm.setDeveloperConnection(replace.matcher(devConnection).replaceAll(newName + "$2"));
        }
    }


    private void addRepository(PomXMLManager pomMng, String id, String url) {
        Optional<Model> optModel = pomMng.getModelFile();
        if (!optModel.isPresent()) {
            return;
        }

        List<Repository> reps = optModel.get().getRepositories();
        Repository repository = new Repository();
        repository.setId(id);
        repository.setUrl(url);
        repository.setLayout(REPOSITORY_LAYOUT);

        if (!isListHasRepository(reps, repository)) {
            reps.add(repository);
        }
    }

    private void modifyRepository(List<Repository> reps, String oldId, String newId, String url) {
        if (reps == null) {
            return;
        }

        for (Repository repository : reps) {
            if (repository != null && repository.getId().equals(oldId)) {
                repository.setId(newId);
                repository.setUrl(url);
                repository.setLayout(REPOSITORY_LAYOUT);
            }
        }
    }

    private void removeRepository(PomXMLManager pomMng, String id) {
        Optional<Model> model = pomMng.getModelFile();
        if (!model.isPresent()) {
            return;
        }
        List<Repository> reps = model.get().getRepositories();
        Repository remRep = null;
        for (Repository repository : reps) {
            if (id.equals(repository.getId())) {
                remRep = repository;
            }
        }
        if (remRep != null) {
            reps.remove(remRep);
        }
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

    private boolean isValidString(String value) {
        return value != null && !value.isEmpty();
    }
}
