package com.lgc.gitlabtool.git.jgit;

/**
 *
 * @author Lyudmila Lysk
 */
public enum ChangedFileStatus {

    ADDED {
        @Override
        public String toString() {
            return "added";
        }
    },

    UNTRACKED {
        @Override
        public String toString() {
            return "untracked";
        }
    },

    MISSING {
        @Override
        public String toString() {
            return "missing";
        }
    },

    REMOVED {
        @Override
        public String toString() {
            return "removed";
        }
    },

    CONFLICTING {
        @Override
        public String toString() {
            return "has conflicts";
        }
    },

    MODIFIED {
        @Override
        public String toString() {
            return "modified";
        }
    },

    CHANGED {
        @Override
        public String toString() {
            return "changed";
        }
    };
}
