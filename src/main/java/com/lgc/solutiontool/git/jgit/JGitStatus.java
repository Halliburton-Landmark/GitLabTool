package com.lgc.solutiontool.git.jgit;

/**
 * Status of JGit operations
 *
 * @author Lyska Lyudmila
 */
public enum JGitStatus {

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

    FAST_FORWARD_SQUASHED {
      @Override
      public String toString() {
        return "Fast-forward-squashed";
      }

      @Override
      public boolean isSuccessful() {
        return true;
      }
    },

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

    MERGED_SQUASHED {
      @Override
      public String toString() {
        return "Merged-squashed";
      }

      @Override
      public boolean isSuccessful() {
        return true;
      }
    },

    MERGED_SQUASHED_NOT_COMMITTED {
      @Override
      public String toString() {
        return "Merged-squashed-not-committed";
      }

      @Override
      public boolean isSuccessful() {
        return true;
      }
    },

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

    ABORTED {
      @Override
      public String toString() {
        return "Aborted";
      }

      @Override
      public boolean isSuccessful() {
        return false;
      }
    },

    MERGED_NOT_COMMITTED {
      @Override
    public String toString() {
        return "Merged-not-committed";
      }

      @Override
      public boolean isSuccessful() {
        return true;
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
    },

    /**
     * Status representing a checkout conflict, meaning that nothing could
     * be merged, as the pre-scan for the trees already failed for certain
     * files (i.e. local modifications prevent checkout of files).
     */
    CHECKOUT_CONFLICT {
      @Override
    public String toString() {
        return "Checkout Conflict";
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
