package com.lgc.solutiontool.git.args;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.lgc.solutiontool.git.args.handlers.CleanArgHandler;
import com.lgc.solutiontool.git.services.ServiceProvider;
import com.lgc.solutiontool.git.services.StorageService;

/**
 * Processes command line arguments.
 * To add supporting of new command line arg do the following:
 * <ol>
 *  <li>Create class that implements Runnable (preffered in args.handlers package)</li>
 *  <li>Add new item to _argHandlers map, where key is your command line arg,
 *  value - instance of the class from previous step</li>
 * </ol>
 *
 * @author Yevhen Strazhko
 */
public class ArgsProcessor {
    /**
     * Cleans application's storage
     */
    private static final String ARG_CLEAN = "-clean";

    private Map<String, Runnable> _argHandlers;

    /**
     * Creates instance
     */
    public ArgsProcessor() {
        initArgHandlersMap();
    }

    private void initArgHandlersMap() {
        _argHandlers = new HashMap<>();
        _argHandlers.put(ARG_CLEAN,
                new CleanArgHandler((StorageService) ServiceProvider.getInstance()
                                     .getService(StorageService.class.getName())));
    }

    /**
     * Processes all command line args.
     * Should be launched at start of the application
     *
     * @param args command line arguments
     */
    public void processArgs(String[] args) {
        Arrays.stream(args)
              .map(_argHandlers::get)
              .filter(Objects::nonNull)
              .forEach(Runnable::run);
    }
}
