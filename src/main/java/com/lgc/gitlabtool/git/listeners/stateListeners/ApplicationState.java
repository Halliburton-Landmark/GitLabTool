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
            return "cloning state";
        }
    },

    PULL {
        @Override
        public String toString() {
            return "pulling state";
        }
    },

    COMMIT {
        @Override
        public String toString() {
            return "commiting state";
        }
    },

    PUSH {
        @Override
        public String toString() {
            return "pushing state";
        }
    },

    CREATE_PROJECT {
        @Override
        public String toString() {
            return "state of creation project";
        }
    }
}
