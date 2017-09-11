package com.lgc.gitlabtool.git.services;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.ProjectStatus;

public class ProjectStatusServiceImpl implements ProjectStatusService {

    private final GitService _gitService;

    public ProjectStatusServiceImpl(GitService gitService) {
        _gitService = gitService;
    }

    @Override
    public void updateProjectStatus(Project project) {
        if (project == null) {
            return;
        }
        String nameBranch = _gitService.getCurrentBranchName(project);

        boolean[] result = _gitService.hasConflictsAndChanges(project);
        boolean hasConflicts = result[0];
        boolean hasChanges = result[1];

        int aheadIndex = 0;
        int behindIndex = 0;
        if (nameBranch != null) {
            int[] indexCount = _gitService.getAheadBehindIndexCounts(project, nameBranch);
            aheadIndex = indexCount[0];
            behindIndex = indexCount[1];
        }

        ProjectStatus projectStatus;
        if (project.getProjectStatus() == null) {
            projectStatus = new ProjectStatus(hasConflicts, hasChanges, aheadIndex, behindIndex, nameBranch);
            project.setProjectStatus(projectStatus);
        } else {
            projectStatus = project.getProjectStatus();
            projectStatus.setCurrentBranch(nameBranch);
            projectStatus.setHasConflicts(hasConflicts);
            projectStatus.setHasChanges(hasChanges);
            projectStatus.setAheadIndex(aheadIndex);
            projectStatus.setBehindIndex(behindIndex);
        }
    }
}
