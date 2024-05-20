package com.arextest.common.serialization;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author jmo
 * @since 2022/1/28
 */
public interface SerializationReader {

    <T> T readValue(InputStream inputStream, Class<T> valueClass) throws IOException;

    <T> T readValue(InputStream inputStream, TypeReference<T> typeReference) throws IOException;
}
