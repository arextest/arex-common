package io.arex.common.utils;

import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jmo
 * @since 2022/1/28
 */
@RunWith(JUnit4.class)
public class SerializationUtilsTest {
    @Test
    public void testUseZstdSerializeToBase64() {
        List<UserTest> userTestList = new ArrayList<>();
        String base64Text = SerializationUtils.useZstdSerializeToBase64(userTestList);
        Assert.assertNotNull(base64Text);
        UserTest[] deResult = SerializationUtils.useZstdDeserialize(base64Text, UserTest[].class);
        Assert.assertNotNull(deResult);
        Assert.assertEquals(0, deResult.length);
        // add item to test
        UserTest userTest = new UserTest();
        userTest.setName("myTestName");
        userTestList.add(userTest);
        base64Text = SerializationUtils.useZstdSerializeToBase64(userTestList);
        Assert.assertNotNull(base64Text);
        deResult = SerializationUtils.useZstdDeserialize(base64Text, UserTest[].class);
        Assert.assertNotNull(deResult);
        Assert.assertEquals(1, deResult.length);
        Assert.assertEquals(userTest.getName(), deResult[0].getName());
    }

    @Test
    public void testUseZstdDeserialize() {
        UserTest userTest = SerializationUtils.useZstdDeserialize(SerializationUtils.EMPTY_INSTANCE, UserTest.class);
        Assert.assertNotNull(userTest);
        Assert.assertNull(userTest.getName());
    }

    @Data
    static class UserTest {
        private String name;
    }
}