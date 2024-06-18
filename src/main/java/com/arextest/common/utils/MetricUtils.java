package com.arextest.common.utils;


import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public final class MetricUtils {
    public static final String GET_METHOD = "GET";
    public static final String SERVICE_NAME_HEADER = "arex-service-name";
    public static final String CATEGORY_TYPE_HEADER = "arex-category-type";
    public static final String ENTRY_PAYLOAD_NAME = "service.entry.payload";
    public static final String TYPE = "type";
    public static final String REQUEST_TAG = "request";
    public static final String RESPONSE_TAG = "response";
    public static final String SERVICE_NAME = "serviceName";
    public static final String PATH = "path";
    public static final String CATEGORY = "category";
    public static final String START_TIME = "startTime";


    private MetricUtils() {

    }

    public static void putIfValueNotEmpty(String value, String tagName, Map<String, String> tags) {
        if (StringUtils.isEmpty(value)) {
            return;
        }
        tags.put(tagName, value);
    }

}
