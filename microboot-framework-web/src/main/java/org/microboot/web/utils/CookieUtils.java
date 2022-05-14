package org.microboot.web.utils;

import org.apache.commons.lang3.StringUtils;
import org.microboot.core.func.FuncV1;

import javax.servlet.http.Cookie;

/**
 * @author 胡鹏
 */
public class CookieUtils {

    public static Cookie newCookie(String key, String value, FuncV1<Cookie> func) throws Exception {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            return null;
        }
        Cookie cookie = new Cookie(key, value);
        if (func != null) {
            func.func(cookie);
        }
        return cookie;
    }
}
