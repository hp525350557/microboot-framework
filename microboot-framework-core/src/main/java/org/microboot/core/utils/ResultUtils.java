package org.microboot.core.utils;

import com.google.common.collect.Maps;
import org.microboot.core.constant.Constant;

import java.util.Map;

/**
 * @author 胡鹏
 */
public class ResultUtils {

    public static Map<String, Object> success() {
        Map<String, Object> result = Maps.newHashMap();
        result.put("code", Constant.CODE_1);
        result.put("msg", null);
        return result;
    }

    public static Map<String, Object> success(String msg) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("code", Constant.CODE_1);
        result.put("msg", msg);
        return result;
    }

    public static Map<String, Object> success(Object data) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("code", Constant.CODE_1);
        result.put("data", data);
        return result;
    }

    public static Map<String, Object> success(String code, String msg) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }

    public static Map<String, Object> success(String code, Object data) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("code", code);
        result.put("data", data);
        return result;
    }

    public static Map<String, Object> success(String code, String msg, Map<String, Object> data) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("code", code);
        result.put("msg", msg);
        result.put("data", data);
        return result;
    }

    public static Map<String, Object> success(String code, Object msg, Map<String, Object> data) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("code", code);
        result.put("msg", msg);
        result.put("data", data);
        return result;
    }


    public static Map<String, Object> success(String msg, Map<String, Object> data) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("code", Constant.CODE_1);
        result.put("msg", msg);
        result.put("data", data);
        return result;
    }

    public static Map<String, Object> success(Object msg, Map<String, Object> data) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("code", Constant.CODE_1);
        result.put("msg", msg);
        result.put("data", data);
        return result;
    }

    public static Map<String, Object> success(Map<String, Object> data) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("code", Constant.CODE_1);
        result.put("data", data);
        return result;
    }

    public static Map<String, Object> error() {
        Map<String, Object> result = Maps.newHashMap();
        result.put("code", Constant.CODE_0);
        result.put("msg", null);
        return result;
    }

    public static Map<String, Object> error(String msg) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("code", Constant.CODE_0);
        result.put("msg", msg);
        return result;
    }

    public static Map<String, Object> error(Object data) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("code", Constant.CODE_0);
        result.put("data", data);
        return result;
    }

    public static Map<String, Object> error(String code, String msg) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }

    public static Map<String, Object> error(String code, Object data) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("code", code);
        result.put("data", data);
        return result;
    }

    public static Map<String, Object> error(String code, String msg, Map<String, Object> data) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("code", code);
        result.put("msg", msg);
        result.put("data", data);
        return result;
    }

    public static Map<String, Object> error(String code, Object msg, Map<String, Object> data) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("code", code);
        result.put("msg", msg);
        result.put("data", data);
        return result;
    }

    public static Map<String, Object> error(String msg, Map<String, Object> data) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("code", Constant.CODE_0);
        result.put("msg", msg);
        result.put("data", data);
        return result;
    }

    public static Map<String, Object> error(Map<String, Object> data) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("code", Constant.CODE_0);
        result.put("data", data);
        return result;
    }

    public static Map<String, Object> result(String key, Object value) {
        Map<String, Object> result = Maps.newHashMap();
        result.put(key, value);
        return result;
    }
}
