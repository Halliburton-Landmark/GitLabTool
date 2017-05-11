package com.lgc.solutiontool.git.services;

import java.util.function.Consumer;

public interface NetworkService {
    
    /**
     * Sends the handshake to the server and returns the response code
     * 
     * @param serverURL - URL of the server
     * @return server's response code
     */
    int getServerResponseCode(String serverURL);

    /**
     * Runs the URL verification in background thread
     * 
     * @param inputURL - URL address typed by user
     * @param handler <code>Consumer</code> realization that obtains response code 
     */
    void runURLVerification(String inputURL, Consumer<Integer> handler);

}
