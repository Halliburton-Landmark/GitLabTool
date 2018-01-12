package com.lgc.gitlabtool.git.jgit;

/**
 * Status for {@link ChangedFile}.
 *
 * It shows us file state: removed, added, it was added to the staging or not.
 *
 * @author Lyudmila Lysk
 */
public enum ChangedFileStatus {

    CONFLICTING {
        @Override
        public String toString() {
            return "has conflicts";
        }
    },

    UNTRACKED {
        @Override
        public String toString() {
            return "untracked";
        }
    },

    MODIFIED {
        @Override
        public String toString() {
            return "modified";
        }
    },

    MISSING {
        @Override
        public String toString() {
            return "missing";
        }
    },

    ADDED {
        @Override
        public String toString() {
            return "added";
        }
    },

    REMOVED {
        @Override
        public String toString() {
            return "removed";
        }
    },

    CHANGED {
        @Override
        public String toString() {
            return "changed";
        }
    };
}
