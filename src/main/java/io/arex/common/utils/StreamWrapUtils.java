package io.arex.common.utils;

import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

/**
 * @author jmo
 * @since 2022/1/27
 */
public final class StreamWrapUtils {
    private StreamWrapUtils() {
    }

    public static byte[] decodeBase64(String base64Source) {
        if (StringUtils.isEmpty(base64Source)) {
            return null;
        }
        return Base64.getDecoder().decode(base64Source);
    }

    public static OutputStream wrapBase64(OutputStream outputStream) throws IOException {
        return Base64.getEncoder().wrap(outputStream);
    }

    public static OutputStream wrapZstdWithBase64(OutputStream outputStream) throws IOException {
        return wrapZstd(wrapBase64(outputStream));
    }

    public static OutputStream wrapZstd(OutputStream outputStream) throws IOException {
        return new ZstdOutputStreamNoFinalizer(outputStream);
    }

    public static InputStream wrapZstd(InputStream inputStream) throws IOException {
        return new ZstdInputStreamNoFinalizer(inputStream);
    }

    public static InputStream wrapBase64(InputStream inputStream) throws IOException {
        return Base64.getDecoder().wrap(inputStream);
    }

    public static InputStream wrapZstdWithBase64(InputStream inputStream) throws IOException {
        return wrapZstd(wrapBase64(inputStream));
    }


    private static class ZstdInputStreamNoFinalizer extends ZstdInputStream {
        public ZstdInputStreamNoFinalizer(InputStream inputStream) throws IOException {
            super(inputStream);
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        @Override
        public void finalize() {
        }
    }

    private static class ZstdOutputStreamNoFinalizer extends ZstdOutputStream {
        public ZstdOutputStreamNoFinalizer(OutputStream outStream) throws IOException {
            super(outStream);
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        @Override
        public void finalize() {
        }
    }
}
