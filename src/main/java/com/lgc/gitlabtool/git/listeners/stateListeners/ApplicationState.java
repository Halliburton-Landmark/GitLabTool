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

    CHECKOUT_BRANCH {
        @Override
        public String toString() {
            return "Checkouting branch";
        }
    },

    CREATE_BRANCH {
        @Override
        public String toString() {
            return "Branch creating";
        }
    },

    EDIT_POM {
        @Override
        public String toString() {
            return "Editing pom.xml file";
        }
    },

    LOAD_PROJECTS {
        @Override
        public String toString() {
            return "Loading projects";
        }
    },

    UPDATE_PROJECT_STATUSES {
        @Override
        public String toString() {
            return "Updating project statuses";
        }
    },

    REVERT {
        @Override
        public String toString() {
            return "Reverting changes";
        }
    },

    ADD_FILES_TO_INDEX {
        @Override
        public String toString() {
            return "Adding files to index";
        }
    },

    RESET {
        @Override
        public String toString() {
            return "Reseting changed files";
        }
    };

    public String getState() {
        return this.toString() + " state";
    }
}
