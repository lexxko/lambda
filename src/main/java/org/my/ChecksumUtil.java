package org.my;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

public final class ChecksumUtil {
    private ChecksumUtil() {
    }

    public static long getCRC32Checksum(byte[] bytes) {
        Checksum checksum = new CRC32();
        checksum.update(bytes, 0, bytes.length);
        return checksum.getValue();
    }
}
