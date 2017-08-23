package com.lgc.gitlabtool.git.ui.javafx;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.JGitStatus;

/**
 * This type of dialog should be used in Gitlab Tool
 * instead of <code>GLTAlert</code> or <code>Alert</code> dialog
 * for displaying information about the status of the operation.
 *
 * @author Igor Khlaponin
 */
public class StatusDialog extends GLTAlert {

    /**
     * If the the dialog message contains more than {@link #MAX_ROW_COUNT_IN_STATUS_DIALOG}
     * we should show the message with count of rows, e.g.
     * <p>
     *     <code>9 of 12 project have been cloned</code>
     * <p>
     */
    public static final int MAX_ROW_COUNT_IN_STATUS_DIALOG = 10;

    private static final String NEW_LINE_SYMBOL = System.getProperty("line.separator");

    /**
     * Creates the instance of this class
     *
     * @param title - the title of the window
     * @param headerText - header of the message
     * @param content - message that should be shown
     */
    public StatusDialog(String title, String headerText, String content) {
        super(title, headerText, content);
    }

    /**
     * Creates the instance of this class with Gitlab Tool icon
     * without content
     *
     * @param title - the title of the window
     * @param headerText - header of the message
     */
    public StatusDialog(String title, String headerText) {
        super(title, headerText, StringUtils.EMPTY);
    }

    /**
     * Sets the content to be shown depends on count of projects.
     * If count of projects is more than {@link #MAX_ROW_COUNT_IN_STATUS_DIALOG}
     * then collapsed message will be shown.
     * Else detailed information about statuses will be shown.
     *
     * @param statuses -        statuses of JGit action
     * @param countOfProjects - total count of selected projects
     * @param formatStrings -   parameters for message that will be shown if count of statuses
     *                          more than {@link #MAX_ROW_COUNT_IN_STATUS_DIALOG}
     * @return massage that will be shown
     */
    public String showMessage(Map<Project, JGitStatus> statuses, int countOfProjects, String... formatStrings) {
        int size = statuses.size();
        if (size > 0 && size < MAX_ROW_COUNT_IN_STATUS_DIALOG) {
            String detailedMessage = statuses.entrySet().stream()
                    .map(pair -> pair.getKey().getName() + " - " + pair.getValue())
                    .collect(Collectors.joining(NEW_LINE_SYMBOL));
            setContentText(detailedMessage);
            return detailedMessage;
        } else {
            String formattedMessage = "";
            if (formatStrings.length == 1) {
                formattedMessage = String.format(formatStrings[0], getSomeOfManySuffix(statuses, countOfProjects));
            } else {
                int paramsSize = formatStrings.length;
                String[] params = Arrays.copyOfRange(formatStrings, 1, paramsSize - 1);
                formattedMessage = String.format(formatStrings[0], params);
            }
            setContentText(formattedMessage);
            return formattedMessage;
        }
    }

    /**
     * Sets the content to be shown depends on count of projects.
     * If count of projects is more than {@link #MAX_ROW_COUNT_IN_STATUS_DIALOG}
     * then collapsed message will be shown.
     * Else detailed information about statuses will be shown.
     *
     * @param statuses -        statuses of action
     * @param countOfProjects - total count of selected projects
     * @param formatStrings -   parameters for message that will be shown if count of statuses
     *                          more than {@link #MAX_ROW_COUNT_IN_STATUS_DIALOG}
     * @return massage that will be shown
     */
    public String showMessageForSimpleStatuses(Map<Project, Boolean> statuses, int countOfProjects, String... formatStrings) {
        int size = statuses.size();
        if (size > 0 && size < MAX_ROW_COUNT_IN_STATUS_DIALOG) {
            String detailedMessage = statuses.entrySet().stream()
                    .map(pair -> pair.getKey().getName() + " - " + convertBooleanToMesasge(pair.getValue()))
                    .collect(Collectors.joining(NEW_LINE_SYMBOL));
            setContentText(detailedMessage);
            return detailedMessage;
        } else {
            String formattedMessage = "";
            if (formatStrings.length == 1) {
                formattedMessage = String.format(formatStrings[0], getSomeOfManySuffixSimpleStatuses(statuses, countOfProjects));
            } else {
                int paramsSize = formatStrings.length;
                String[] params = Arrays.copyOfRange(formatStrings, 1, paramsSize - 1);
                formattedMessage = String.format(formatStrings[0], params);
            }
            setContentText(formattedMessage);
            return formattedMessage;
        }
    }

    private String convertBooleanToMesasge(boolean status){
        if (status) {
            return "Successful";
        } else {
            return "Failed";
        }
    }

    private String getSomeOfManySuffix(Map<Project, JGitStatus> statuses, int countOfProjects) {
        int countOfSuccessfulStatuses =
                (int) statuses.entrySet().stream()
                .map(pair -> pair.getValue())
                .filter(status -> status.equals(JGitStatus.SUCCESSFUL))
                .count();
        return countOfSuccessfulStatuses + " of " + countOfProjects;
    }

    private String getSomeOfManySuffixSimpleStatuses(Map<Project, Boolean> statuses, int countOfProjects) {
        int countOfSuccessfulStatuses =
                (int) statuses.entrySet().stream()
                        .map(pair -> pair.getValue())
                        .filter(status -> status.equals(true))
                        .count();
        return countOfSuccessfulStatuses + " of " + countOfProjects;
    }

}
