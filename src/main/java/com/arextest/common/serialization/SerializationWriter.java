package com.arextest.common.serialization;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author jmo
 * @since 2022/1/28
 */
public interface SerializationWriter {
    <T> void writeValue(OutputStream outputStream, T value) throws IOException;
}
