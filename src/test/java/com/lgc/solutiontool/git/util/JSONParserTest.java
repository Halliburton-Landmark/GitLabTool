package com.lgc.solutiontool.git.util;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class JSONParserTest {

    @Test
    public void parseToMapCorrectDataTest() {
        System.err.println("parseToMapCorrectDataTest");
        String json = "{\"id\":1348279,\"name\":\"apitest_group\",\"path\":\"apitest_group\","
                + "\"description\":\"\",\"visibility_level\":20,\"ldap_cn\":null,\"ldap_access\":null,"
                + "\"projects\":[{\"id\":2935605,\"description\":\"\",\"default_branch\":\"master\","
                + "\"tag_list\":[],\"public\":true,\"archived\":false,\"visibility_level\":20,"
                + "\"ssh_url_to_repo\":\"git@gitlab.com:apitest_group/test.git\",\"http_url_to_repo"
                + "\":\"https://gitlab.com/apitest_group/test.git\",\"web_url\":"
                + "\"https://gitlab.com/apitest_group/test\"}]}";

        Map<String, Object> map = JSONParser.parseToMap(json);

        Assert.assertNotNull(map);
        Assert.assertNotNull(map.get("projects"));
        Assert.assertEquals("apitest_group", map.get("name"));
        Assert.assertEquals(1348279, ((Double) map.get("id")).intValue());

        Assert.assertTrue(JSONParser.parseToMap("{}").isEmpty());
        Assert.assertTrue(JSONParser.parseToMap("[]").isEmpty());
    }

    @Test
    public void parseToMapIncorrectDataTest() {
        System.err.println("parseToMapIncorrectDataTest");
        Assert.assertNull(JSONParser.parseToMap(""));
        Assert.assertNull(JSONParser.parseToMap("8mdf 99485jg"));
        Assert.assertNull(JSONParser.parseToMap(null));
    }


}
