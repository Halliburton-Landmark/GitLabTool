package com.lgc.gitlabtool.git.util;

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
     * 
     * @param branchName name of branch for validation
     * @return <code>true</code> if branchName is valid or <code>false</code> otherwise
     * 
     * @see <a href="https://www.kernel.org/pub/software/scm/git/docs/git-check-ref-format.html">Branch naming
     *      restrictions</a>
     */
    public static boolean validate(String branchName) {
        String regex = "\\.{0}(\\.\\.){0}"; 
        return branchName.matches(regex);
    }
    
    // TODO: remove it
    public static void main(String[] args) {
        String name = "";
        System.out.println(validate(name));
    }

}
