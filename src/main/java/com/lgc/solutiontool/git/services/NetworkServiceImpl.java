package com.lgc.solutiontool.git.services;

import java.net.URL;
import java.util.function.Consumer;

import javax.net.ssl.HttpsURLConnection;

import com.lgc.solutiontool.git.util.RequestType;
import com.lgc.solutiontool.git.util.URLManager;

public class NetworkServiceImpl implements NetworkService {

    @Override
    public int getServerResponseCode(String serverURL) {
        int responseCode = -1;
        try {
            // handshake
            URL obj = new URL(URLManager.completeServerURL(serverURL));
            HttpsURLConnection connection = (HttpsURLConnection) obj.openConnection();
            connection.setReadTimeout(10000); // timeout after 10 seconds
            connection.setRequestMethod(RequestType.GET.toString());
            responseCode = connection.getResponseCode();
        } catch (Exception e) {
            return -1;
        }
        return responseCode;
    }
    
    @Override
    public void runURLVerification(String inputURL, Consumer<Integer> handler) {
        Runnable runnable = () -> {
            String url = URLManager.trimServerURL(inputURL);
            int responseCode = getServerResponseCode(url);
            handler.accept(responseCode);
        };
        new Thread(runnable).start();
    }

}
