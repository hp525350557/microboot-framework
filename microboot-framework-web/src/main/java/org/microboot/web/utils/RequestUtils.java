package org.microboot.web.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import org.apache.commons.lang3.StringUtils;
import org.microboot.core.utils.ConvertUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * @author 胡鹏
 */
public class RequestUtils {

    /**
     * 从header,cookie,request中获取指定key的参数
     *
     * @param request
     * @param key
     * @return
     */
    public static String getParameter(HttpServletRequest request, String key) {
        String value = request.getParameter(key);

        if (StringUtils.isBlank(value)) {
            value = request.getHeader(key);
        }

        Cookie[] cookies = request.getCookies();
        if (StringUtils.isBlank(value) && cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (!StringUtils.equals(cookie.getName(), key)) {
                    continue;
                }
                value = cookie.getValue();
                break;
            }
        }

        return value;
    }

    /**
     * 获取get或post请求的参数
     *
     * @param request
     * @return
     */
    public static String getParameterString(HttpServletRequest request) {
        String value;
        String requestMethod = request.getMethod();
        String requestType = request.getHeader("X-Requested-With");
        if (StringUtils.equalsIgnoreCase(RequestMethod.GET.name(), requestMethod)
                && !StringUtils.equalsIgnoreCase("XMLHttpRequest", requestType)
                && !StringUtils.startsWith(request.getContentType(), MediaType.APPLICATION_JSON_VALUE)
        ) {
            value = request.getQueryString();
        } else {
            Map<String, String[]> parameterMap = request.getParameterMap();
            value = ConvertUtils.object2Json(parameterMap);
        }
        return value;
    }

    /**
     * 获取get或post请求的参数
     *
     * @param request
     * @return
     */
    public static Map<String, Object> getParameter(HttpServletRequest request) {
        String value;
        String requestMethod = request.getMethod();
        String requestType = request.getHeader("X-Requested-With");
        if (StringUtils.equalsIgnoreCase(RequestMethod.GET.name(), requestMethod)
                && !StringUtils.equalsIgnoreCase("XMLHttpRequest", requestType)
                && !StringUtils.startsWith(request.getContentType(), MediaType.APPLICATION_JSON_VALUE)
        ) {
            value = request.getQueryString();
            Map<String, Object> parameterMap = ConvertUtils.uriVars2Map(value);
            value = ConvertUtils.object2Json(parameterMap);
        } else {
            Map<String, String[]> parameterMap = request.getParameterMap();
            value = ConvertUtils.object2Json(parameterMap);
        }
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return ConvertUtils.json2Map(value);
    }

    /**
     * 获取header
     *
     * @param request
     * @return
     */
    public static Map<String, Object> getHeader(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null) {
            return null;
        }
        Map<String, Object> header = Maps.newHashMap();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            header.put(key, value);
        }
        return header;
    }

    /**
     * 获取ip
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 获取浏览器信息
     *
     * @param request
     * @return
     */
    public static String getBrowserInfo(HttpServletRequest request) {
        //获取浏览器信息
        Browser browser = UserAgent.parseUserAgentString(request.getHeader("User-Agent")).getBrowser();
        //获取浏览器版本号
        List<String> browserInfoList = Lists.newArrayList();
        if (browser != null) {
            if (browser.getManufacturer() != null) {
                browserInfoList.add(browser.getManufacturer().getName());
            }
            if (browser.getRenderingEngine() != null) {
                browserInfoList.add(browser.getRenderingEngine().getName());
            }
            if (StringUtils.isNotBlank(browser.getName())) {
                browserInfoList.add(browser.getName());
            }
            Version version = browser.getVersion(request.getHeader("User-Agent"));
            if (version != null) {
                browserInfoList.add(version.getVersion());
            }
        }
        return ConvertUtils.listStr2Json(browserInfoList);
    }

    /**
     * 获取url
     *
     * @param request
     * @return
     */
    public static String getUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
