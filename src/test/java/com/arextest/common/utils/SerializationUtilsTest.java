package com.arextest.common.utils;

import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author jmo
 * @since 2022/1/28
 */
@RunWith(JUnit4.class)
public class SerializationUtilsTest {
    @Test
    public void testUseZstdDeserialize() {
        User userTest = SerializationUtils.useZstdDeserialize(SerializationUtils.EMPTY_INSTANCE, User.class);
        Assert.assertNotNull(userTest);
        Assert.assertNull(userTest.getName());
    }

    @Test
    public void testSimpleItem() {
        User userTest = buildOneTestItem();
        String base64Text = SerializationUtils.useZstdSerializeToBase64(userTest);
        Assert.assertNotNull(base64Text);

        User deResult = SerializationUtils.useZstdDeserialize(base64Text, User.class);
        assertOne(userTest, deResult);
    }

    @Test
    public void testRecursiveItem() {
        User userTest = buildOneTestItem();
        User child = buildOneTestItem();
        userTest.setChild(child);
        userTest.setChildren(Arrays.asList(buildOneTestItem(), buildOneTestItem()));

        String base64Text = SerializationUtils.useZstdSerializeToBase64(userTest);
        Assert.assertNotNull(base64Text);

        User deResult = SerializationUtils.useZstdDeserialize(base64Text, User.class);
        assertOne(userTest, deResult);
        assertOne(child, deResult.getChild());
        deResult.getChildren().forEach(one -> assertOne(child,one));
    }

    @Test
    public void testArray() {
        User userTest = buildOneTestItem();
        String base64Text = SerializationUtils.useZstdSerializeToBase64(new User[]{userTest});
        Assert.assertNotNull(base64Text);

        User[] deResult = SerializationUtils.useZstdDeserialize(base64Text, User[].class);
        Assert.assertNotNull(deResult);
        Assert.assertNotNull(deResult[0]);
        User deResultItem = deResult[0];
        assertOne(userTest, deResultItem);
    }

    @Test
    public void testList() {
        User userTest = buildOneTestItem();
        String base64Text = SerializationUtils.useZstdSerializeToBase64(Collections.singletonList(userTest));
        Assert.assertNotNull(base64Text);

        User[] deResult = SerializationUtils.useZstdDeserialize(base64Text, User[].class);
        Assert.assertNotNull(deResult);
        Assert.assertNotNull(deResult[0]);
        User deResultItem = deResult[0];
        assertOne(userTest, deResultItem);
    }

    private static User buildOneTestItem() {
        User userTest = new User();
        userTest.setName("myTestName");
        userTest.setAge(18);
        userTest.setId(1L);
        userTest.setStrList(Collections.singletonList("TEST STR"));
        return userTest;
    }

    private static void assertOne(User userTest, User deResult) {
        Assert.assertNotNull(deResult);
        Assert.assertEquals(userTest.getName(), deResult.getName());
        Assert.assertEquals(userTest.getId(), deResult.getId());
        Assert.assertEquals(userTest.getAge(), deResult.getAge());
        Assert.assertEquals(userTest.getStrList(), deResult.getStrList());
    }

    @Data
    static class User {
        private String name;
        private Integer age;
        private Long id;
        private List<String> strList;
        private User child;
        private List<User> children;
    }
}