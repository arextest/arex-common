package com.arextest.common.utils;

import com.arextest.common.serialization.SerializationProviders;
import com.arextest.common.serialization.SerializationReader;
import com.arextest.common.serialization.SerializationWriter;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author jmo
 * @since 2021/11/8
 */

@Slf4j
public final class SerializationUtils {
    private SerializationUtils() {
    }

    /**
     * The bytes of zstd  equal json string "{}",useful deserialize
     */
    public static final byte[] EMPTY_INSTANCE = new byte[]{40, -75, 47, -3, 0, 80, 17, 0, 0, 123, 125};

    public static <T> void useZstdSerializeTo(OutputStream outputStream, T value) {
        useZstdSerializeTo(SerializationProviders.DEFAULT_PROVIDER, outputStream, value);
    }

    public static <T> void useZstdSerializeTo(SerializationWriter serializationWriter, OutputStream outputStream,
                                              T value) {
        try (OutputStream zstdOutputStream = StreamWrapUtils.wrapZstd(outputStream)) {
            serializationWriter.writeValue(zstdOutputStream, value);
        } catch (IOException e) {
            LOGGER.error("serialize error:{}", e.getMessage(), e);
        }
    }

    public static <T> byte[] useZstdSerializeToBytes(T value) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            useZstdSerializeTo(out, value);
            return out.toByteArray();
        } catch (IOException e) {
            LOGGER.error("serialize error:{}", e.getMessage(), e);
        }
        return null;
    }

    public static <T> String useZstdSerializeToBase64(T value) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            useZstdSerializeTo(StreamWrapUtils.wrapBase64(out), value);
            return out.toString(StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            LOGGER.error("serialize error:{}", e.getMessage(), e);
        }
        return null;
    }

    public static <T> T useZstdDeserialize(InputStream inputStream, Class<T> clazz) {
        return useZstdDeserialize(SerializationProviders.DEFAULT_PROVIDER, inputStream, clazz);
    }

    public static <T> T useZstdDeserialize(SerializationReader serializationReader, InputStream inputStream,
                                           Class<T> clazz) {
        try (InputStream zstdStream = StreamWrapUtils.wrapZstd(inputStream)) {
            return serializationReader.readValue(zstdStream, clazz);
        } catch (Throwable e) {
            LOGGER.error("deserialize error:{}, target:{}", e.getMessage(), clazz.getName(), e);
        }
        return null;
    }

    public static <T> T useZstdDeserialize(String base64Source, Class<T> clazz) {
        return useZstdDeserialize(StreamWrapUtils.decodeBase64(base64Source), clazz);
    }

    public static <T> T useZstdDeserialize(SerializationReader serializationReader, byte[] zstdValues, Class<T> clazz) {
        if (zstdValues == null) {
            return null;
        }
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(zstdValues)) {
            return useZstdDeserialize(serializationReader, inputStream, clazz);
        } catch (Throwable e) {
            LOGGER.error("deserialize error:{}, target:{}", e.getMessage(), clazz.getName(), e);
        }
        return null;
    }

    public static <T> T useZstdDeserialize(byte[] zstdValues, Class<T> clazz) {
        return useZstdDeserialize(SerializationProviders.DEFAULT_PROVIDER, zstdValues, clazz);
    }

    public static <T> T useZstdDeserialize(SerializationReader serializationReader, byte[] zstdValues,
        TypeReference<T> typeReference) {
        if (zstdValues == null) {
            return null;
        }
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(zstdValues)) {
            return useZstdDeserialize(serializationReader, inputStream, typeReference);
        } catch (Exception e) {
            LOGGER.error("deserialize error:{}", e.getMessage(), e);
        }
        return null;
    }

    public static <T> T useZstdDeserialize(SerializationReader serializationReader, InputStream inputStream,
        TypeReference<T> typeReference) {
        try (InputStream zstdStream = StreamWrapUtils.wrapZstd(inputStream)) {
            return serializationReader.readValue(zstdStream, typeReference);
        } catch (Exception e) {
            LOGGER.error("deserialize error:{}", e.getMessage(), e);
        }
        return null;
    }
}
