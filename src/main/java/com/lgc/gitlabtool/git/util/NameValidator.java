package com.lgc.gitlabtool.git.util;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameValidator {

    private static final NameValidator _validator;
    static {
        _validator = new NameValidator();
    }

    private NameValidator(){
    }

    public static NameValidator get() {
        return _validator;
    }

    /**
     * Validates the branch name according to the next rules.
     * <p>
     * A branch name cannot:
     * <li>Have a path component that begins with "."</li>
     * <li>Have a double dot ".."</li>
     * <li>End with a "/"</li>
     * <li>End with ".lock"</li>
     * <li>Contain an ASCII control character, "~", "^", ":" or SP</li>
     * <li>Contain a "\" (backslash)</li>
     * <li>Contain whitespace</li>
     * <li>Contain "@" character or "@{" sequence</li>
     * <li>Contain "?", asterisk "*", or open bracket "["</li>
     *
     * @param branchName name of branch for validation
     * @return <code>true</code> if branchName is valid or <code>false</code> otherwise
     *
     * @see <a href="https://www.kernel.org/pub/software/scm/git/docs/git-check-ref-format.html">Branch naming
     *      restrictions</a>
     *      <p>
     *      <a href="https://git-scm.com/docs/git-check-ref-format">look on git</a>
     */
    public boolean validateBranchName(String branchName) {
        if (branchName.startsWith(".")) {
            return false;
        }
        if (branchName.endsWith(".lock") || branchName.endsWith("/")) {
            return false;
        }
        List<String> restrictedCharacters = Arrays.asList("~", "^", ":", " ", "?", "@", "*", "[", "\\", "..");
        Optional<String> matches = restrictedCharacters.stream()
                .filter(e -> branchName.contains(e))
                .findFirst();

        return !matches.isPresent();
    }

    /**
     * Name can contain only letters, digits, '_', '.', dash, space. It must start with letter, digit or '_'.
     * Path can contain only letters, digits, '_', '-' and '.'. Cannot start with '-' or end in '.', '.git' or '.atom'.
     *
     * @param projectName the name of project for validation
     * @return <code>true</code> if project name is valid or <code>false</code> otherwise
     */
    public boolean validateProjectName(String projectName) {
        if (projectName == null || projectName.isEmpty() ) {
            return false;
        }
        if (projectName.startsWith("-")) {
            return false;
        }
        if (projectName.endsWith(".") || projectName.endsWith(".git") || projectName.endsWith(".atom")) {
            return false;
        }
        Pattern p = Pattern.compile("[a-zA-Z0-9._-]+");
        Matcher m = p.matcher(projectName);
        return m.matches();
    }

}
