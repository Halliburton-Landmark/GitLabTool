package com.lgc.gitlabtool.git.util;

import java.util.Arrays;

public class BranchValidator {
    
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
    public boolean validate(String branchName) {
        if (branchName.startsWith(".")) {
            return false;
        }
        if (branchName.endsWith(".lock") || branchName.endsWith("/")) {
            return false;
        }
        String[] restrictedCharacters = { "~", "^", ":", " ", "?", "@", "*", "[", "\\", ".." };
        int countOfErrors = (int) Arrays.asList(restrictedCharacters).stream()
                .filter(e -> branchName.contains(e))
                .count();

        return countOfErrors == 0;
    }

}
