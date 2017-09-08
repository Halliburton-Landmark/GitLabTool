package com.lgc.gitlabtool.git.listeners.stateListeners;

/**
 * These states duplicate the operations, which the application can execute.
 *
 * @author Lyudmila Lyska
 */
public enum ApplicationState {
    CLONE {

        @Override
        public String toString() {
            return "Cloning";
        }
    },

    PULL {
        @Override
        public String toString() {
            return "Pulling";
        }
    },

    COMMIT {
        @Override
        public String toString() {
            return "Commiting";
        }
    },

    PUSH {
        @Override
        public String toString() {
            return "Pushing";
        }
    },

    CREATE_PROJECT {
        @Override
        public String toString() {
            return "Project creation";
        }
    },

    SWITCH_BRANCH {
        @Override
        public String toString() {
            return "Branch switching";
        }
    },

    EDIT_POM {
        @Override
        public String toString() {
            return "Editing pom.xml file";
        }
    },

    REFRESH_PROJECTS {
        @Override
        public String toString() {
            return "Refresh projects";

        }
    };

    public String getState() {
        return this.toString() + " state";
    }
}
