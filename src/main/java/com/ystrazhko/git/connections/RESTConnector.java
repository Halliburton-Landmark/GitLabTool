package com.ystrazhko.git.connections;

import java.util.Map;

public interface RESTConnector {
    String URL_MAIN_PART = "https://gitlab.com/api/v3";

    Object sendPost(String url, Map<String, String> params, Map<String, String> header, String reguest);
}
