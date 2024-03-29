package rpc.core.compress.gzip;

import rpc.core.compress.Compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipCompress implements Compress {

    private static final int BUFFER_SIZE = 1024 * 4;

    @Override
    public byte[] compress(byte[] in) {
        if (!Optional.ofNullable(in).isPresent()) {
            throw new NullPointerException("The byte buffer need to be compressed is null");
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPOutputStream gzipOut = new GZIPOutputStream(out)) {
            gzipOut.write(in);
            gzipOut.flush();
            gzipOut.finish();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Gzip compress error", e);
        }
    }

    @Override
    public byte[] decompress(byte[] in) {
        if (!Optional.ofNullable(in).isPresent()) {
            throw new NullPointerException("The byte buffer need to be decompressed is null");
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPInputStream gzipOut = new GZIPInputStream(new ByteArrayInputStream(in))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int n;
            while ((n = gzipOut.read(buffer)) > -1) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Gzip decompress error", e);
        }
    }
}
