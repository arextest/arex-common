package com.arextest.common.serialization;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

/**
 * @author jmo
 * @since 2022/1/28
 */
public final class SerializationProviders {
    private SerializationProviders() {
    }

    private static final int ONE_K_BUFFER_SIZE = 1024;
    private static final ObjectMapper DEFAULT_JACKSON_MAPPER = new ObjectMapper();

    public static final SerializationProvider DEFAULT_PROVIDER = jacksonProvider(DEFAULT_JACKSON_MAPPER);
    public static final SerializationProvider UTF8_TEXT_PROVIDER = utf8TextProvider();

    public static SerializationProvider jacksonProvider(ObjectMapper objectMapper) {
        return new JacksonSerializationProvider(objectMapper);
    }

    private static SerializationProvider utf8TextProvider() {
        return new Utf8StringSerializationProvider();
    }

    private static class Utf8StringSerializationProvider implements SerializationProvider {

        @Override
        public <T> void writeValue(OutputStream outputStream, T value) throws IOException {
            if (value instanceof String) {
                StringReader stringReader = new StringReader((String) value);
                IOUtils.copy(stringReader, outputStream, StandardCharsets.UTF_8);
                outputStream.close();
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T readValue(InputStream inputStream, Class<T> valueClass) throws IOException {
            if (valueClass != String.class) {
                return null;
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream(ONE_K_BUFFER_SIZE);
            byte[] buffer = new byte[ONE_K_BUFFER_SIZE];
            int n;
            while ((n = inputStream.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return (T) out.toString(StandardCharsets.UTF_8.name());
        }
    }

    private static class JacksonSerializationProvider implements SerializationProvider {
        private final ObjectMapper jacksonMapper;

        private JacksonSerializationProvider(ObjectMapper jacksonMapper) {
            this.jacksonMapper = jacksonMapper;
            this.jacksonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }

        @Override
        public <T> void writeValue(OutputStream outputStream, T value) throws IOException {
            jacksonMapper.writeValue(outputStream, value);
        }

        @Override
        public <T> T readValue(InputStream inputStream, Class<T> valueClass) throws IOException {
            return jacksonMapper.readValue(inputStream, valueClass);
        }
    }
}
