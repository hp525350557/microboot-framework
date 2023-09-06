package org.microboot.core.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.util.UUID;

/**
 * @author 胡鹏
 */
public class UUIDUtils {

    public static String uuidFor32(byte[] name) {
        return UUID.nameUUIDFromBytes(name).toString().replace("-", "");
    }

    public static String uuidFor36(byte[] name) {
        return UUID.nameUUIDFromBytes(name).toString();
    }

    public static String uuidFor32() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String uuidFor36() {
        return UUID.randomUUID().toString();
    }

    public static String uuidForLeastNum(String... prefixArray) {
        String value = Long.toString(Math.abs(UUID.randomUUID().getLeastSignificantBits()));
        if (ArrayUtils.isEmpty(prefixArray)) {
            return value;
        }
        StringBuilder sb = new StringBuilder();
        for (String prefix : prefixArray) {
            sb.append(prefix);
        }
        sb.append(value);
        return sb.toString();
    }

    public static String uuidForMostNum(String... prefixArray) {
        String value = Long.toString(Math.abs(UUID.randomUUID().getMostSignificantBits()));
        if (ArrayUtils.isEmpty(prefixArray)) {
            return value;
        }
        StringBuilder sb = new StringBuilder();
        for (String prefix : prefixArray) {
            sb.append(prefix);
        }
        sb.append(value);
        return sb.toString();
    }
}
