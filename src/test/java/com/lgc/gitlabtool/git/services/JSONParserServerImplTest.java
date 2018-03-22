package com.lgc.gitlabtool.git.services;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.reflect.TypeToken;
import com.lgc.gitlabtool.git.entities.Group;


/**
 * Tests for the JSONParserTest.
 *
 * @author Lyudmila Lyska
 */
public class JSONParserServerImplTest {

    private static final String groupJson = "{\"id\":1348279,\"name\":\"apitest_group\",\"path\":\"apitest_group\","
            + "\"description\":\"\",\"visibility_level\":20,\"ldap_cn\":null,\"ldap_access\":null,"
            + "\"projects\":[{\"id\":2935605,\"description\":\"\",\"default_branch\":\"master\","
            + "\"tag_list\":[],\"public\":true,\"archived\":false,\"visibility_level\":20,"
            + "\"ssh_url_to_repo\":\"git@gitlab.com:apitest_group/test.git\",\"http_url_to_repo"
            + "\":\"https://gitlab.com/apitest_group/test.git\",\"web_url\":"
            + "\"https://gitlab.com/apitest_group/test\"}]}";

    private static final Type typeListGroups = new TypeToken<List<Group>>() {}.getType();
    private static final String groupsJson = "[" + groupJson + ", " + groupJson + "]";

    private Map<String, Object> getGroupMap() {
        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put("id", 1348279);
        groupMap.put("name", "apitest_group");
        groupMap.put("path", "apitest_group");
        groupMap.put("description", "");
        groupMap.put("projects", "[{\"id\":2935605,\"description\":\"\",\"default_branch\":\"master\","
                + "\"tag_list\":[],\"public\":true,\"archived\":false,\"visibility_level\":20,"
                + "\"ssh_url_to_repo\":\"git@gitlab.com:apitest_group/test.git\",\"http_url_to_repo"
                + "\":\"https://gitlab.com/apitest_group/test.git\",\"web_url\":"
                + "\"https://gitlab.com/apitest_group/test\"}]");

        return groupMap;
    }

    private Group getTestingGroup() {
        Group group = new Group();
        group.setClonedStatus(true);
        group.setPath(".");
        return group;
    }

    @Test
    public void parseToMapCorrectDataTest() {

        Map<String, Object> map = getJSONParserServer().parseToMap(groupJson);

        Assert.assertNotNull(map);
        Assert.assertNotNull(map.get("projects"));
        Assert.assertEquals("apitest_group", map.get("name"));
        Assert.assertEquals(1348279, ((Double) map.get("id")).intValue());

        Assert.assertTrue(getJSONParserServer().parseToMap("{}").isEmpty());
        Assert.assertTrue(getJSONParserServer().parseToMap("[]").isEmpty());
    }

    @Test
    public void parseToMapIncorrectDataTest() {
        Assert.assertNull(getJSONParserServer().parseToMap(""));
        Assert.assertNull(getJSONParserServer().parseToMap("8mdf 99485jg"));
        Assert.assertNull(getJSONParserServer().parseToMap(null));
    }

    @Test
    public void parseToJsonFromMapCorrectDataTest() {
        String result = getJSONParserServer().parseMapToJson(getGroupMap());
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("\"path\":\"apitest_group\""));
        Assert.assertNotNull(getJSONParserServer().parseMapToJson(Collections.emptyMap()));
    }

    @Test
    public void parseToJsonFromMapIncorrectDataTest() {
        Assert.assertTrue(getJSONParserServer().parseMapToJson(null).isEmpty());
    }

    @Test
    public void parseToJsonFromObjectCorrectDataTest() {
        String result = getJSONParserServer().parseObjectToJson(getTestingGroup());
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("\"_isCloned\":true"));
    }

    @Test
    public void parseToJsonFromObjectIncorrectDataTest() {
        String result = getJSONParserServer().parseObjectToJson(null);
        Assert.assertNull(result);
    }

    @Test
    public void parseToObjectCorrectDataTest() {
        Group gr = getJSONParserServer().parseToObject(groupJson, Group.class);
        Assert.assertNotNull(gr);
        Assert.assertEquals(1348279, gr.getId());

        Assert.assertNotNull(getJSONParserServer().parseToObject("{}", Group.class));
    }

    @Test
    public void parseToObjectIncorrectDataTest() {
        Assert.assertNull(getJSONParserServer().parseToObject(null, Group.class));
        Assert.assertNull(getJSONParserServer().parseToObject(null, null));
        Assert.assertNull(getJSONParserServer().parseToObject(groupJson, null));
        Assert.assertNull(getJSONParserServer().parseToObject("756hghf dfu yhs", Group.class));
        Assert.assertNull(getJSONParserServer().parseToObject("[]", Group.class));
    }

    @Test
    public void parseToCollectionCorrectDataTest() {
        Collection<Group> grs = getJSONParserServer().parseToCollectionObjects(groupsJson, typeListGroups);

        Assert.assertNotNull(grs);
        Assert.assertEquals(2, grs.size());

        grs = getJSONParserServer().parseToCollectionObjects("[]", typeListGroups);
        Assert.assertTrue(grs.isEmpty());
    }

    @Test
    public void parseToCollectionIncorrectDataTest() {
        List<Object> emptyList = new ArrayList<>();

        Assert.assertEquals(getJSONParserServer().parseToCollectionObjects(null, typeListGroups), emptyList);
        Assert.assertEquals(getJSONParserServer().parseToCollectionObjects(groupsJson, null), emptyList);
        Assert.assertEquals(getJSONParserServer().parseToCollectionObjects(null, null), emptyList);
        Assert.assertEquals(getJSONParserServer().parseToCollectionObjects("76437 jhj 31", typeListGroups), emptyList);
        Assert.assertEquals(getJSONParserServer().parseToCollectionObjects("{}", typeListGroups), emptyList);
    }

    private JSONParserService getJSONParserServer() {
        return new JSONParserServiceImpl();
    }
}
