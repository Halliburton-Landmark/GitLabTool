package com.lgc.gitlabtool.git.main;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.lgc.gitlabtool.git.connections.RESTConnector;
import com.lgc.gitlabtool.git.ui.UserInterface;
import com.lgc.gitlabtool.git.ui.javafx.JavaFXUI;

public class Main {
    public static void main(String[] args) {
        final UserInterface ui = new JavaFXUI();
        detectProxy();
        ui.run(args);
    }

    private static void detectProxy() {
        System.setProperty("java.net.useSystemProxies", "true");
        System.out.println("detecting proxies");
        List<?> l = null;
        try {
            l = ProxySelector.getDefault().select(new URI(RESTConnector.URL_MAIN_PART));
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (l != null) {
            for (Object name : l) {
                java.net.Proxy proxy = (java.net.Proxy) name;
                System.out.println("proxy type: " + proxy.type());

                InetSocketAddress addr = (InetSocketAddress) proxy.address();

                if (addr == null) {
                    System.out.println("No Proxy");
                } else {
                    System.out.println("proxy hostname: " + addr.getHostName());
                    System.setProperty("http.proxyHost", addr.getHostName());
                    System.out.println("proxy port: " + addr.getPort());
                    System.setProperty("http.proxyPort", Integer.toString(addr.getPort()));
                }
            }
        }
    }
}
