package com.lgc.gitlabtool.git.services;

import java.util.function.Consumer;

public interface NetworkService {
    
    /**
     * Runs the URL verification in background thread
     * 
     * @param inputURL - URL address typed by user
     * @param handler <code>Consumer</code> realization that obtains response code 
     */
    void runURLVerification(String inputURL, Consumer<Integer> handler);

}
