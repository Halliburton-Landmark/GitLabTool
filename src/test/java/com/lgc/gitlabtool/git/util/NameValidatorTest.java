package com.lgc.gitlabtool.git.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class NameValidatorTest {

    private final String testData;
    private final boolean expectedResult;
    
    public NameValidatorTest(String testData, boolean expectedResult) {
        this.testData = testData;
        this.expectedResult = expectedResult;
    }
    
    @Test
    public void inputIsValid() {
        boolean actualResult = new NameValidator().validateBranchName(testData);
        
        assertEquals(expectedResult, actualResult);
    }
    
    @Parameterized.Parameters
    public static List<Object[]> validationData() {
        return Arrays.asList(new Object[][] {
            {"sdNd", true},
            {"asdf-asdf-Nds", true},
            {"HJsdHJK", true},
            {"=-234", true},
            {".sdf", false},          // dot on the beginning of the row
            {"adsf..df", false},      // contains double dot
            {"asdf/", false},         // slash at the end of the row
            {"asdf/asdf", true},
            {"asdf.lock", false},     // .lock at the end of the row
            {"asdf.lock.foo", true},
            {"adsf~", false},         // contains ~ character
            {"~asd", false},
            {"^sdaf", false},         // contains ^ character
            {"ad^", false},
            {":sdf", false},          // contains : character
            {"asdf:", false},
            {" ", false},             // contains spaces
            {" sdf", false},
            {"sdf ", false},
            {"sdf adsf", false},
            {"adf\\", false},         // contains back slash
            {"sdf@", false},          // contains @ character
            {"sdf@adsf", false},
            {"asdf@{", false},
            {"?sdf", false},          // contains ? character
            {"adsf?d", false},
            {"adf*", false},          // contains * character
            {"*asd", false},
            {"[", false},             // contains [ character
            {"asdf[", false}
        });
    }
}
