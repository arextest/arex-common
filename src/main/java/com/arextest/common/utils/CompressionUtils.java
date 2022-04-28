package com.arextest.common.utils;

import com.arextest.common.serialization.SerializationProviders;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author jmo
 * @since 2021/11/7
 */
@Slf4j
public final class CompressionUtils {

    private CompressionUtils() {

    }

    public static String useZstdCompress(String source) {
        if (StringUtils.isEmpty(source)) {
            return source;
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStream base64Stream = StreamWrapUtils.wrapBase64(out)) {
            SerializationUtils.useZstdSerializeTo(SerializationProviders.UTF8_TEXT_PROVIDER, base64Stream, source);
            return out.toString(StandardCharsets.UTF_8.name());
        } catch (Throwable e) {
            LOGGER.error("zstd compress error:{},source:{}", e.getMessage(), source, e);
        }
        return null;
    }

    public static String useZstdDecompress(String base64Source) {
        if (StringUtils.isEmpty(base64Source)) {
            return base64Source;
        }
        byte[] base64Bytes = StreamWrapUtils.decodeBase64(base64Source);
        return SerializationUtils.useZstdDeserialize(SerializationProviders.UTF8_TEXT_PROVIDER, base64Bytes,
                String.class);
    }

    public static String encodeToBase64String(byte[] value) {
        if (value == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(value);
    }
}
