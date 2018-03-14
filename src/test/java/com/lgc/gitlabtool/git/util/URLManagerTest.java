package com.lgc.gitlabtool.git.util;

import static junit.framework.TestCase.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class URLManagerTest {

    private final String url;
    private final boolean expectedResult;

    public URLManagerTest(String testedURL, boolean expectedResult) {
        this.url = testedURL;
        this.expectedResult = expectedResult;
    }

    @Test
    public void isURLValid() {
        boolean actualResult = URLManager.isURLValid(url);

        assertEquals(expectedResult, actualResult);
    }

    @Parameterized.Parameters
    public static List<Object[]> validationData() {
        return Arrays.asList(new Object[][] {
                {"http://gitlab.com", true},
                {"https://gitlab.com", true},
                {"http://gitlab.com/api/v4", true},
                {"https://gitlab.com/api/v4", true},
                {"gitlab.com/api/v4", true},
                {"gitlab.com", true},
                {"https://gitlab.com/api/v4/", true},
                {"https://gitlab.com/", true},
                {"gitlab.com/api/v4/", true},
                {"gitlab.com/", true},

                {"http:/gitlab.com", false},
                {"https:/gitlab.com", false},
                {"https//gitlab.com", false},
                {"https://gi-tlab.com", false},
                {"https://gitlab.com-", false},
                {"gitlab.com/foo", false},
                {"gitlab.com/foo/foo", false},
                {"gitlab.com/foo/foo/", false},
                {"/gitlab.com", false},
                {"", false}
        });
    }
}
