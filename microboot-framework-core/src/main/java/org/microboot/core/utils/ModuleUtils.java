package org.microboot.core.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author 胡鹏
 */
public class ModuleUtils {

    public static String getModuleName() {
        String moduleDir = ModuleUtils.class.getResource("/").toString().replace("/target/classes/", "");
        return StringUtils.substring(moduleDir, StringUtils.lastIndexOf(moduleDir, "/") + 1);
    }
}
