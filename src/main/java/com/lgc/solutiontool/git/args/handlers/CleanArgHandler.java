package com.lgc.solutiontool.git.args.handlers;

import com.lgc.solutiontool.git.services.StorageService;

/**
 * Handles -clean command line argument.
 * Cleans application storage.

 * @author Yevhen Strazhko
 */
public class CleanArgHandler implements Runnable {

    private final StorageService _storageService;

    /**
     * Creates instance
     *
     * @param service current instance of storage service
     */
    public CleanArgHandler(StorageService service) {
        _storageService = service;
    }

    @Override
    public void run() {
        System.out.println("Clearing storage...");
        _storageService.clearStorage();
        System.out.println("Storage is cleaned");
    }

}
