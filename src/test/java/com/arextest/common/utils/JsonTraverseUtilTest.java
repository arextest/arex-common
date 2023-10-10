package com.arextest.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Created by qzmo on 2023-10-09
 */
@RunWith(JUnit4.class)
public class JsonTraverseUtilTest {
    @Test
    public void testSimpleObj() throws JsonProcessingException {
        String in = "{\"a\":1,\"b\":2,\"c\":3}";
        String out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("{\"a\":null,\"b\":null,\"c\":null}", out);

        // test different data type
        in = "{\"a\":1,\"b\":2,\"c\":3,\"d\":true,\"e\":false,\"f\":null,\"g\":\"string\",\"h\":1.1}";
        out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("{\"a\":null,\"b\":null,\"c\":null,\"d\":null,\"e\":null,\"f\":null,\"g\":null,\"h\":null}", out);
    }

    @Test
    public void testRecursiveObj() throws JsonProcessingException {
        String in = "{\"a\":1,\"b\":2,\"c\":3,\"d\":{\"a\":1,\"b\":2,\"c\":3}}";
        String out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("{\"a\":null,\"b\":null,\"c\":null,\"d\":{\"a\":null,\"b\":null,\"c\":null}}", out);

        // test deeply recursive obj
        in = "{\"a\":1,\"b\":2,\"c\":3,\"d\":{\"a\":1,\"b\":2,\"c\":3,\"d\":{\"a\":1,\"b\":2,\"c\":3}}}";
        out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("{\"a\":null,\"b\":null,\"c\":null,\"d\":{\"a\":null,\"b\":null,\"c\":null,\"d\":{\"a\":null,\"b\":null,\"c\":null}}}", out);
    }

    @Test
    public void testArrayNode() throws JsonProcessingException {
        String in = "[{\"a\":1,\"b\":2,\"c\":3},{\"a\":1,\"b\":2,\"c\":3}]";
        String out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("[{\"a\":null,\"b\":null,\"c\":null},{\"a\":null,\"b\":null,\"c\":null}]", out);

        // test obj with array node
        in = "{\"a\":1,\"b\":2,\"c\":3,\"d\":[{\"a\":1,\"b\":2,\"c\":3},{\"a\":1,\"b\":2,\"c\":3}]}";
        out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("{\"a\":null,\"b\":null,\"c\":null,\"d\":[{\"a\":null,\"b\":null,\"c\":null},{\"a\":null,\"b\":null,\"c\":null}]}", out);
    }

    @Test
    public void testNull() throws JsonProcessingException {
        String in = null;
        JsonTraverseUtils.trimAllLeaves(in);

        in = "";
        JsonTraverseUtils.trimAllLeaves(in);

        in = "null";
        String out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("null", out);

        in = "{}";
        out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("{}", out);

        in = "[]";
        out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("[]", out);

        in = "[null,null,null]";
        out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("[null,null,null]", out);

        in = "[null,null,null,{\"a\":1,\"b\":2,\"c\":null}]";
        out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("[null,null,null,{\"a\":null,\"b\":null,\"c\":null}]", out);

        in = "{\"a\":1,\"b\":2,\"c\":null}";
        out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("{\"a\":null,\"b\":null,\"c\":null}", out);
    }

    @Test
    public void testArray() throws JsonProcessingException {
        String in = "[1,2,3]";
        String out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("[null,null,null]", out);

        in = "[1,null,3]";
        out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("[null,null,null]", out);

        // test obj arr
        in = "[{\"a\":1,\"b\":2,\"c\":3},{\"a\":1,\"b\":2,\"c\":3}]";
        out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("[{\"a\":null,\"b\":null,\"c\":null},{\"a\":null,\"b\":null,\"c\":null}]", out);

        // test arr with elements missing fields
        in = "[{\"a\":1,\"b\":2,\"c\":3},{\"a\":1,\"b\":2}]";
        out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("[{\"a\":null,\"b\":null,\"c\":null},{\"a\":null,\"b\":null}]", out);

        // test obj arr with null
        in = "[{\"a\":1,\"b\":2,\"c\":3},null,{\"a\":1,\"b\":2,\"c\":3}]";
        out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("[{\"a\":null,\"b\":null,\"c\":null},null,{\"a\":null,\"b\":null,\"c\":null}]", out);

        // test object with arr fields
        in = "{\"a\":1,\"b\":2,\"c\":3,\"d\":[{\"a\":1,\"b\":2,\"c\":3},{\"a\":1,\"b\":2,\"c\":3}]}";
        out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("{\"a\":null,\"b\":null,\"c\":null,\"d\":[{\"a\":null,\"b\":null,\"c\":null},{\"a\":null,\"b\":null,\"c\":null}]}", out);

        // test nested array node
        in = "[{\"a\":1,\"b\":2,\"c\":3},{\"a\":1,\"b\":2,\"c\":3,\"d\":[{\"a\":1,\"b\":2,\"c\":3},{\"a\":1,\"b\":2,\"c\":3}]}]";
        out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("[{\"a\":null,\"b\":null,\"c\":null},{\"a\":null,\"b\":null,\"c\":null,\"d\":[{\"a\":null,\"b\":null,\"c\":null},{\"a\":null,\"b\":null,\"c\":null}]}]", out);

        // test 2d array
        in = "[[1,2,3],[1,2,3]]";
        out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("[[null,null,null],[null,null,null]]", out);

        // test 3d array
        in = "[[[1,2,3],[1,2,3]],[[1,2,3],[1,2,3]]]";
        out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("[[[null,null,null],[null,null,null]],[[null,null,null],[null,null,null]]]", out);

        // test array with empty object
        in = "[{\"a\":1,\"b\":2,\"c\":3},{}]";
        out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("[{\"a\":null,\"b\":null,\"c\":null},{}]", out);
    }

    @Test
    public void testActualRequest() throws JsonProcessingException {
        String in = "{\"orderID\": 9999999999,\"flightClass\":\"N\",\"source\":\"Distribution\",\"createUser\":\"19e\",\"refundOrderFlightInfo\":[{\"passengerName\":\"测试用户\",\"sequence\":1,\"rbkID\":0,\"rebookingListID\":0,\"refundType\":\"自愿\",\"refundScene\":0,\"isInv\":false,\"isCanCalculate\":true,\"refund\":\"103\",\"refundRate\":\"0.6\",\"serverFee\":\"0\",\"usedAmount\":\"0\",\"usedCost\":\"0\",\"usedTax\":\"0\",\"refundTag\":0,\"canDeduct\":false,\"serviceProductType\":0,\"servicePackageDeductFee\":\"0\",\"speicalEventConfigID\":0,\"emdId\":0,\"emdAmount\":\"0\"}]}";
        String out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("{\"orderID\":null,\"flightClass\":null,\"source\":null,\"createUser\":null,\"refundOrderFlightInfo\":[{\"passengerName\":null,\"sequence\":null,\"rbkID\":null,\"rebookingListID\":null,\"refundType\":null,\"refundScene\":null,\"isInv\":null,\"isCanCalculate\":null,\"refund\":null,\"refundRate\":null,\"serverFee\":null,\"usedAmount\":null,\"usedCost\":null,\"usedTax\":null,\"refundTag\":null,\"canDeduct\":null,\"serviceProductType\":null,\"servicePackageDeductFee\":null,\"speicalEventConfigID\":null,\"emdId\":null,\"emdAmount\":null}]}", out);

        in = "{\n" +
                "    \"orderID\": 9999999999,\n" +
                "    \"flightClass\": \"N\",\n" +
                "    \"source\": \"Distribution\",\n" +
                "    \"createUser\": \"19e\",\n" +
                "    \"refundOrderFlightInfo\": [\n" +
                "        {\n" +
                "            \"passengerName\": \"测试用户\",\n" +
                "            \"sequence\": 1,\n" +
                "            \"rbkID\": 0,\n" +
                "            \"rebookingListID\": 0,\n" +
                "            \"refundType\": \"自愿\",\n" +
                "            \"refundScene\": 0,\n" +
                "            \"isInv\": false,\n" +
                "            \"isCanCalculate\": true,\n" +
                "            \"refund\": \"103\",\n" +
                "            \"refundRate\": \"0.6\",\n" +
                "            \"serverFee\": \"0\",\n" +
                "            \"usedAmount\": \"0\",\n" +
                "            \"usedCost\": \"0\",\n" +
                "            \"usedTax\": \"0\",\n" +
                "            \"refundTag\": 0,\n" +
                "            \"canDeduct\": false,\n" +
                "            \"serviceProductType\": 0,\n" +
                "            \"servicePackageDeductFee\": \"0\",\n" +
                "            \"speicalEventConfigID\": 0,\n" +
                "            \"emdId\": 0,\n" +
                "            \"emdAmount\": \"0\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        out = JsonTraverseUtils.trimAllLeaves(in);
        Assert.assertEquals("{\"orderID\":null,\"flightClass\":null,\"source\":null,\"createUser\":null,\"refundOrderFlightInfo\":[{\"passengerName\":null,\"sequence\":null,\"rbkID\":null,\"rebookingListID\":null,\"refundType\":null,\"refundScene\":null,\"isInv\":null,\"isCanCalculate\":null,\"refund\":null,\"refundRate\":null,\"serverFee\":null,\"usedAmount\":null,\"usedCost\":null,\"usedTax\":null,\"refundTag\":null,\"canDeduct\":null,\"serviceProductType\":null,\"servicePackageDeductFee\":null,\"speicalEventConfigID\":null,\"emdId\":null,\"emdAmount\":null}]}", out);
    }
}
