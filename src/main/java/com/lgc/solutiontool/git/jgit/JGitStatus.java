package com.lgc.solutiontool.git.jgit;

/**
 * Status of JGit operations
 *
 * @author Lyska Lyudmila
 */
public enum JGitStatus {

    SUCCESSFUL {
        @Override
        public String toString() {
          return "Successful";
        }

        @Override
        public boolean isSuccessful() {
          return true;
        }
    },

    FAST_FORWARD {
      @Override
      public String toString() {
        return "Fast-forward";
      }

      @Override
      public boolean isSuccessful() {
        return true;
      }
    },

    /**
     * All the changes from the branch you’re trying to merge have already been merged
     * to the branch you’re currently on.
     */
    ALREADY_UP_TO_DATE {
      @Override
      public String toString() {
        return "Already-up-to-date";
      }

      @Override
      public boolean isSuccessful() {
        return true;
      }
    },

    /**
     * Operation failed
     */
    FAILED {
      @Override
      public String toString() {
        return "Failed";
      }

      @Override
      public boolean isSuccessful() {
        return false;
      }
    },

    /**
     *
     */
    MERGED {
      @Override
      public String toString() {
        return "Merged";
      }

      @Override
      public boolean isSuccessful() {
        return true;
      }
    },

    /**
     * There are conflicts in the local repository.
     * It is necessary to fix and merge them.
     */
    CONFLICTING {
      @Override
      public String toString() {
        return "Conflicting";
      }

      @Override
      public boolean isSuccessful() {
        return false;
      }
    },

    /**
     * The branch has unsaved changes that can lead to conflicts.
     */
    CONFLICTS {
      @Override
      public String toString() {
        return "Conflicts";
      }

      @Override
      public boolean isSuccessful() {
        return false;
      }
    },

    NOT_SUPPORTED {
      @Override
      public String toString() {
        return "Not-yet-supported";
      }

      @Override
      public boolean isSuccessful() {
        return false;
      }
    };

    /**
     * @return whether the status indicates a successful result
     */
    public abstract boolean isSuccessful();

    /**
     * Gets status by name
     * @param value status name
     * @throws IllegalArgumentException() if an invalid value is passed
     * @return status or throw exception if an invalid value is passed
     */
    public static JGitStatus getStatus(String value) {
        if (value != null) {
            for(JGitStatus status : values()) {
                if(status.toString().equalsIgnoreCase(value)) {
                    return status;
                }
            }
        }
        throw new IllegalArgumentException();
    }
}
