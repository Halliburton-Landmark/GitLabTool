package com.lgc.gitlabtool.git.jgit;

/**
 *
 *
 * @author Lyudmila Lyska
 */
public class Stash {
    private String _name;
    private String _message;

    /**
     *
     *
     * @param name
     * @param message
     */
    public Stash (String name, String message) {
        setName(name);
        setMessage(message);
    }

    /**
     *
     * @return
     */
    public String getName() {
        return _name;
    }

    /**
     *
     * @return
     */
    public String getMessage() {
        return _message;
    }

    private void setName(String name) {
        _name = name;
    }

    private void setMessage(String message) {
        _message = message == null ? "N/A" : message;
    }


}
