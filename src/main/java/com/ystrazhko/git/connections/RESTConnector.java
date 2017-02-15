package com.ystrazhko.git.connections;

import java.util.Map;

public interface RESTConnector {
    Object sendPost(String url, Map<String, String> params);
}
