package com.lgc.solutiontool.git.util;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.reflect.TypeToken;
import com.lgc.solutiontool.git.entities.Group;

public class JSONParserTest {

    private static final String groupJson = "{\"id\":1348279,\"name\":\"apitest_group\",\"path\":\"apitest_group\","
            + "\"description\":\"\",\"visibility_level\":20,\"ldap_cn\":null,\"ldap_access\":null,"
            + "\"projects\":[{\"id\":2935605,\"description\":\"\",\"default_branch\":\"master\","
            + "\"tag_list\":[],\"public\":true,\"archived\":false,\"visibility_level\":20,"
            + "\"ssh_url_to_repo\":\"git@gitlab.com:apitest_group/test.git\",\"http_url_to_repo"
            + "\":\"https://gitlab.com/apitest_group/test.git\",\"web_url\":"
            + "\"https://gitlab.com/apitest_group/test\"}]}";

    private static final Type typeListGroups = new TypeToken<List<Group>>() {}.getType();
    private static final String groupsJson = "[" + groupJson + ", " + groupJson + "]";
    private static final Map<String, Object> groupMap;
    private static final Group group;

    static {
        groupMap = new HashMap<>();
        groupMap.put("id", 1348279);
        groupMap.put("name", "apitest_group");
        groupMap.put("path", "apitest_group");
        groupMap.put("description", "");
        groupMap.put("projects", "[{\"id\":2935605,\"description\":\"\",\"default_branch\":\"master\","
                + "\"tag_list\":[],\"public\":true,\"archived\":false,\"visibility_level\":20,"
                + "\"ssh_url_to_repo\":\"git@gitlab.com:apitest_group/test.git\",\"http_url_to_repo"
                + "\":\"https://gitlab.com/apitest_group/test.git\",\"web_url\":"
                + "\"https://gitlab.com/apitest_group/test\"}]");

        group = new Group();
        group.setClonedStatus(true);
        group.setPathToClonedGroup("."); //TODO
    }

    @Test
    public void parseToMapCorrectDataTest() {

        Map<String, Object> map = JSONParser.parseToMap(groupJson);

        Assert.assertNotNull(map);
        Assert.assertNotNull(map.get("projects"));
        Assert.assertEquals("apitest_group", map.get("name"));
        Assert.assertEquals(1348279, ((Double) map.get("id")).intValue());

        Assert.assertTrue(JSONParser.parseToMap("{}").isEmpty());
        Assert.assertTrue(JSONParser.parseToMap("[]").isEmpty());
    }

    @Test
    public void parseToMapIncorrectDataTest() {
        Assert.assertNull(JSONParser.parseToMap(""));
        Assert.assertNull(JSONParser.parseToMap("8mdf 99485jg"));
        Assert.assertNull(JSONParser.parseToMap(null));
    }

    @Test
    public void parseToJsonFromMapCorrectDataTest() {
        String result = JSONParser.parseToJson(groupMap);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("\"path\":\"apitest_group\""));
        Assert.assertNotNull(JSONParser.parseToJson(Collections.emptyMap()));
    }

    @Test
    public void parseToJsonFromMapIncorrectDataTest() {
        Assert.assertNull(JSONParser.parseToJson(null));
    }

    @Test
    public void parseToJsonFromObjectCorrectDataTest() {
        String result = JSONParser.parseToJson(group);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("\"_isCloned\":true"));
    }

    @Test
    public void parseToJsonFromObjectIncorrectDataTest() {
        String result = JSONParser.parseToJson(null);
        Assert.assertNull(result);
    }

    @Test
    public void parseToObjectCorrectDataTest() {
        Group gr = JSONParser.parseToObject(groupJson, Group.class);
        Assert.assertNotNull(gr);
        Assert.assertNotNull(gr.getProjects());
        Assert.assertEquals(1348279, gr.getId());

        Assert.assertNotNull(JSONParser.parseToObject("{}", Group.class));
    }

    @Test
    public void parseToObjectIncorrectDataTest() {
        Assert.assertNull(JSONParser.parseToObject(null, Group.class));
        Assert.assertNull(JSONParser.parseToObject(null, null));
        Assert.assertNull(JSONParser.parseToObject(groupJson, null));
        Assert.assertNull(JSONParser.parseToObject("756hghf dfu yhs", Group.class));
        Assert.assertNull(JSONParser.parseToObject("[]", Group.class));
    }

    @Test
    public void parseToCollectionCorrectDataTest() {
        Collection<Group> grs = JSONParser.parseToCollectionObjects(groupsJson, typeListGroups);

        Assert.assertNotNull(grs);
        Assert.assertEquals(2, grs.size());

        grs = JSONParser.parseToCollectionObjects("[]", typeListGroups);
        Assert.assertTrue(grs.isEmpty());
    }

    @Test
    public void parseToCollectionIncorrectDataTest() {


    }
}
