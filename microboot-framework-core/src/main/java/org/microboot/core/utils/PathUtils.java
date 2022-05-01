package org.microboot.core.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.regex.Matcher;

/**
 * @author 胡鹏
 */
public class PathUtils {

    public static String regular(String path, boolean replaceBeforeAndAfter) {
        if (StringUtils.isBlank(path)) {
            return "";
        }
        String separator = Matcher.quoteReplacement(File.separator);
        path = path.replaceAll("\\+", separator);
        path = path.replaceAll("/+", separator);
        if (replaceBeforeAndAfter) {
            path = path.replaceAll("^" + separator + "*|" + separator + "*$", "");
        }
        path = path.replaceAll(separator + "+", separator);
        return path;
    }
}
