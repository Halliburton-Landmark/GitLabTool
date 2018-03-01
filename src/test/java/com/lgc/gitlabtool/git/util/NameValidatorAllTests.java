package com.lgc.gitlabtool.git.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the NameValidator.
 *
 * @author Lyudmila Lyska
 */
public class NameValidatorAllTests {

    /**
     * Name can contain only letters, digits, '_', '.', dash, space. It must start with letter, digit or '_'.
     * Path can contain only letters, digits, '_', '-' and '.'. Cannot start with '-' or end in '.', '.git' or '.atom'.
     *
     * @return <code>true</code> if project name is valid or <code>false</code> otherwise
     */
    @Test
    public void validateProjectNameIncorrectDataTest() {
        NameValidator validator = NameValidator.get();

        Assert.assertFalse(validator.validateProjectName(null));
        Assert.assertFalse(validator.validateProjectName(""));
        Assert.assertFalse(validator.validateProjectName("-test"));
        Assert.assertFalse(validator.validateProjectName("test."));
        Assert.assertFalse(validator.validateProjectName("test.git"));
        Assert.assertFalse(validator.validateProjectName("test.atom"));
        Assert.assertFalse(validator.validateProjectName("?Test"));
        Assert.assertFalse(validator.validateProjectName("texst&"));
        Assert.assertFalse(validator.validateProjectName("Test///"));
        Assert.assertFalse(validator.validateProjectName("8+test"));
    }

    @Test
    public void validateProjectNameCorrectDataTest() {
        NameValidator validator = NameValidator.get();

        Assert.assertTrue(validator.validateProjectName("test"));
        Assert.assertTrue(validator.validateProjectName("test.2"));
        Assert.assertTrue(validator.validateProjectName("dsg-test"));
        Assert.assertTrue(validator.validateProjectName("unknown_test"));
        Assert.assertTrue(validator.validateProjectName("666_Test"));
    }
}
