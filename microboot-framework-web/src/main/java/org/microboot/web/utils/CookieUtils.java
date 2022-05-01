package org.microboot.web.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;

/**
 * @author 胡鹏
 */
public class CookieUtils {

    public static Cookie newCookie(String key, String value, String comment, String domain, Boolean httpOnly,
                                   Integer maxAge, String path, Boolean secure, String newValue) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            return null;
        }
        Cookie cookie = new Cookie(key, value);
        if (StringUtils.isNotBlank(comment)) {
            cookie.setComment(comment);
        }
        if (StringUtils.isNotBlank(domain)) {
            cookie.setDomain(domain);
        }
        if (httpOnly != null) {
            cookie.setHttpOnly(httpOnly);
        }
        if (maxAge != null) {
            cookie.setMaxAge(maxAge);
        }
        if (StringUtils.isNotBlank(path)) {
            cookie.setPath(path);
        }
        if (secure != null) {
            cookie.setSecure(secure);
        }
        if (StringUtils.isNotBlank(newValue)) {
            cookie.setValue(newValue);
        }
        return cookie;
    }
}
