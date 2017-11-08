package com.lgc.gitlabtool.git.services;

import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for StateServiceImpl
 *
 * @author Yevhen Strazhko
 */
public class StateServiceImplTest {

    /**
     * Tests that problem described here https://gitlab.com/SolutionToolGitLab/stg/issues/216
     * is fixed
     */
    @Test
    public void testForIssue216() {
        StateServiceImpl stateService = new StateServiceImpl();
        ApplicationState testedState = ApplicationState.CLONE;
        Assert.assertFalse(stateService.isActiveState(testedState));
        stateService.stateOFF(testedState);
        stateService.stateOFF(testedState);
        stateService.stateOFF(testedState);
        stateService.stateOFF(testedState);
        stateService.stateON(testedState);
        Assert.assertTrue(stateService.isActiveState(testedState));
    }
}
