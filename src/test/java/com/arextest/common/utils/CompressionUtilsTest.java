package com.arextest.common.utils;

import com.arextest.common.serialization.SerializationProviders;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author jmo
 * @since 2022/1/28
 */
@RunWith(JUnit4.class)
public class CompressionUtilsTest {

    @Test
    public void testUseZstdCompress() throws Exception {
        String source = "AAAAAAAAAAAAAAAAAAAAAAAaaBBBBBBBBBBBBBBBBBBBBDDDDDDDDDDDDDD";
        String actResult = CompressionUtils.useZstdCompress(source);
        Assert.assertNotEquals(source, actResult);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SerializationProviders.UTF8_TEXT_PROVIDER.writeValue(StreamWrapUtils.wrapZstdWithBase64(out), source);
        String act = out.toString(StandardCharsets.UTF_8.name());
        Assert.assertEquals(actResult, act);

        String decodeResult = CompressionUtils.useZstdDecompress(actResult);
        Assert.assertEquals(source, decodeResult);
    }

    @Test
    public void testUseZstdDecompress() {
        String content = "KLUv/QBQLQoANlRIOUCHOAA0+Q/Mw+S1PQg84s14G1dbjkmAvHgvGLrS71P" +
                "+i5Bkt0d2wCiVzVq1wYBvsCSiwbwTW8T0BjUAQQA3AJD45ip5qnRWhwgJkUc8ZQFi8IjnemK47kTlvOr2atyHvHWNr65KOZvW4SOerTnJSZf7ExABGjUQwOPgfAk6n0ZTssDwRKIOQAHPo6DrHCCFNh4H9IE+pY1RJ2DkdWh8ngtvQAXp8ZnejPvwk8ezxlGVbhLWHgZVb5MeXvL4ypnqolhyVanTktuFKRazPDz1eMxiknpZ2x4+g8CCUecLbbwPgu7xXO11YMxyUTkDEZCDCMX2AikU4rPZEs4aGKjKQ+XxzVlfFzmrw3Nd7onrNmvz8MxVbu82ycxpYQq3GFuP5wsAFEAVAOK3tYnbCn4cMVb68lYa2hF8a3V0AgkXWz/mHg==";
        String decodeResult = CompressionUtils.useZstdDecompress(content);
        content = "[{\"reasonCode\":1,\"domestic\":true,\"reparationsCategory\":1049,\"reparationsReason\":1763," +
                "\"costCenter\":4,\"adjustType\":\"G\",\"remark\":\"航变无忧保障服务赔付客人航变后退改损失\"," +
                "\"responsibleParty\":\"业务政策\",\"auditFlag\":\"T\",\"needRefund\":\"T\",\"notSceneTicket\":\"F\"," +
                "\"source\":\"delayRebookGuarantee\",\"operateType\":\"Create\",\"confirmer\":\"SYSTEM\"," +
                "\"comeFrom\":8},{\"reasonCode\":1,\"domestic\":false,\"reparationsCategory\":1072," +
                "\"reparationsReason\":1764,\"costCenter\":4,\"adjustType\":\"G\",\"remark\":\"航变无忧保障服务赔付客人航变后退改损失\"," +
                "\"responsibleParty\":\"业务政策\",\"auditFlag\":\"T\",\"needRefund\":\"T\",\"notSceneTicket\":\"F\"," +
                "\"source\":\"delayRebookGuarantee\",\"operateType\":\"Create\",\"confirmer\":\"SYSTEM\"," +
                "\"comeFrom\":8}]";
        Assert.assertEquals(content, decodeResult);
    }
}