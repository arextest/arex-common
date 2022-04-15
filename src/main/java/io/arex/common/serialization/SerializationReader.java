package io.arex.common.serialization;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author jmo
 * @since 2022/1/28
 */
public interface SerializationReader {

    <T> T readValue(InputStream inputStream, Class<T> valueClass) throws IOException;
}
