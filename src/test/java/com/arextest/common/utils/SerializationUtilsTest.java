package com.arextest.common.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author jmo
 * @since 2022/1/28
 */
@RunWith(JUnit4.class)
public class SerializationUtilsTest {
    // data compressed by zstd-jni 1.3.7-3
    private static final String OLD_DATA = "KLUv/QBYdQwAVhxbJvDsaMgImV+q2hOqqk9eO0ewT94GfRKRFnQ3t0Lu+M+zsDgsDi8ETQBVAE0AufZrB4wQh8cFehiJ4gL1bPfQzYPpVTzmtdLMLeqWfo2zfcxrBMUbKPYses01Xrd/v1HPvp4qvOabgy/0xGzNf+PY7J/2654tYWVOTwSwAaPs2a/lq19XIgMVByUxYhUnhmSAUk0mlSMimUgqlOSQFhVkUoBJKMhkKBkD91hUx0RRqcHxhVRNnCpSUqImqRMrKj1SCsHZPq32sV+/RXq2208OEUbpNBinQWQL2w2Ai7AR0XstKF969mP3UEXm2tav/entuOUeZkGenu3B1soVu8YQXQyjPdsxiVc89vcHe6kCc//0rI5xrGIlp4SMhgyhezfcL9QaCkHPtvL1fsH+Lf1CLRAJ9Gyb0Q7NuAgjjFBKGXlDzi6JC8if6gTOP/a6h3DRWA4A3MSAFKMRvT3g+MYvhkVcgDRFuIc0YbtxjwoAim04yRQAsZqFbBne1gaRcasHE/AbERWMSLMvdQY=";
    @Test
    public void testUseZstdDeserialize() {
        TestUser userTest = SerializationUtils.useZstdDeserialize(SerializationUtils.EMPTY_INSTANCE, TestUser.class);
        Assert.assertNotNull(userTest);
        Assert.assertNull(userTest.getName());
    }

    @Test
    public void testSimpleItem() {
        TestUser userTest = buildOneTestItem();
        String base64Text = SerializationUtils.useZstdSerializeToBase64(userTest);
        Assert.assertNotNull(base64Text);

        TestUser deResult = SerializationUtils.useZstdDeserialize(base64Text, TestUser.class);
        assertOne(userTest, deResult);
    }

    @Test
    public void testRecursiveItem() {
        TestUser userTest = buildOneTestItem();
        TestUser child = buildOneTestItem();
        userTest.setChild(child);
        userTest.setChildren(Arrays.asList(buildOneTestItem(), buildOneTestItem()));

        String base64Text = SerializationUtils.useZstdSerializeToBase64(userTest);
        Assert.assertNotNull(base64Text);

        TestUser deResult = SerializationUtils.useZstdDeserialize(base64Text, TestUser.class);
        assertOne(userTest, deResult);
        assertOne(child, deResult.getChild());
        deResult.getChildren().forEach(one -> assertOne(child,one));
    }

    @Test
    public void testArray() {
        TestUser userTest = buildOneTestItem();
        String base64Text = SerializationUtils.useZstdSerializeToBase64(new TestUser[]{userTest});
        Assert.assertNotNull(base64Text);

        TestUser[] deResult = SerializationUtils.useZstdDeserialize(base64Text, TestUser[].class);
        Assert.assertNotNull(deResult);
        Assert.assertNotNull(deResult[0]);
        TestUser deResultItem = deResult[0];
        assertOne(userTest, deResultItem);
    }

    @Test
    public void testList() {
        TestUser userTest = buildOneTestItem();
        String base64Text = SerializationUtils.useZstdSerializeToBase64(Collections.singletonList(userTest));
        Assert.assertNotNull(base64Text);

        TestUser[] deResult = SerializationUtils.useZstdDeserialize(base64Text, TestUser[].class);
        Assert.assertNotNull(deResult);
        Assert.assertNotNull(deResult[0]);
        TestUser deResultItem = deResult[0];
        assertOne(userTest, deResultItem);
    }


    @Test
    public void testConcurrentCompress() {
        List<CompletableFuture<Void>> tests = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tests.add(CompletableFuture.runAsync(this::testSimpleItem));
        }
        CompletableFuture.allOf(tests.toArray(new CompletableFuture[0])).join();
    }

    private static TestUser buildOneTestItem() {
        TestUser userTest = new TestUser();
        userTest.setName("myTestName");
        userTest.setAge(18);
        userTest.setId(1L);
        userTest.setStrList(Collections.singletonList("TEST STR"));
        return userTest;
    }

    private static void assertOne(TestUser userTest, TestUser deResult) {
        Assert.assertNotNull(deResult);
        Assert.assertEquals(userTest.getName(), deResult.getName());
        Assert.assertEquals(userTest.getId(), deResult.getId());
        Assert.assertEquals(userTest.getAge(), deResult.getAge());
        Assert.assertEquals(userTest.getStrList(), deResult.getStrList());
    }

    @Data
    static class TestUser {
        private String name;
        private Integer age;
        private Long id;
        private List<String> strList;
        private TestUser child;
        private List<TestUser> children;
    }

    @Test
    public void testOldVersionData() {
        TestDataRoot testDataRoot = SerializationUtils.useZstdDeserialize(OLD_DATA, TestDataRoot.class);
        Assert.assertNotNull(testDataRoot);
        Assert.assertEquals(testDataRoot.getAttributes().getHttpMethod(), "GET");
        Assert.assertEquals(testDataRoot.getAttributes().getHeaders().getAcceptLanguage(), "zh-CN,zh;q=0.9");
        Assert.assertEquals(testDataRoot.getAttributes().getHeaders().getContentType(), "application/json");

        testDataRoot = SerializationUtils.useZstdDeserialize(SerializationUtils.useZstdSerializeToBase64(testDataRoot), TestDataRoot.class);
        Assert.assertNotNull(testDataRoot);
        Assert.assertEquals(testDataRoot.getAttributes().getHttpMethod(), "GET");
        Assert.assertEquals(testDataRoot.getAttributes().getHeaders().getAcceptLanguage(), "zh-CN,zh;q=0.9");
        Assert.assertEquals(testDataRoot.getAttributes().getHeaders().getContentType(), "application/json");
    }


    @Data
    static class TestDataRoot {
        public String body;
        public Attributes attributes;
        public String type;
        @Data
        static class Attributes{
            @JsonProperty("RequestPath")
            public String requestPath;
            @JsonProperty("Headers")
            public Headers headers;
            @JsonProperty("HttpMethod")
            public String httpMethod;
        }

        @Data
        static class Headers {
            @JsonProperty("accept-language")
            public String acceptLanguage;
            public String cookie;
            public String host;
            public String connection;
            @JsonProperty("content-type")
            public String contentType;
            @JsonProperty("accept-encoding")
            public String acceptEncoding;
            @JsonProperty("user-agent")
            public String userAgent;
            public String accept;
        }
    }
}