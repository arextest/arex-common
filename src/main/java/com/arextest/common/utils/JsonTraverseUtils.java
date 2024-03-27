package com.arextest.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;

public class JsonTraverseUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static String trimAllLeaves(String in) throws JsonProcessingException {
        if (StringUtils.isEmpty(in)) return in;
        JsonNode root = OBJECT_MAPPER.readTree(in);
        trimNode(null, root, null);
        return root.toString();
    }

    private static void trimNode(String fieldName, JsonNode node, JsonNode parent) {
        if (node == null || node.isNull()) return;

        // object field
        if (node.isObject()) {
            node.fields().forEachRemaining(field -> trimNode(field.getKey(), field.getValue(), node));

        // array field
        } else if (node.isArray()) {
            int idx = 0;
            Iterator<JsonNode> arrIter = node.elements();

            while (arrIter.hasNext()) {
                JsonNode currentElement = arrIter.next();
                if (currentElement.isObject() || currentElement.isArray()) {
                    // obj node in an array is considered as root node
                    trimNode(null, currentElement, null);
                } else {
                    // leaf node in an array
                    ((ArrayNode) node).set(idx, JsonNodeFactory.instance.nullNode());
                }
                idx++;
            }

        // leaf node
        } else {
            ((ObjectNode) parent).putNull(fieldName);
        }
    }
}
