package com.arextest.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;

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
        if (node.isObject()) {
            node.fields().forEachRemaining(field -> trimNode(field.getKey(), field.getValue(), node));
        } else if (node.isArray()) {
            node.elements().forEachRemaining(element -> trimNode(null, element, null));
        } else {
            ((ObjectNode) parent).putNull(fieldName);
        }
    }
}
