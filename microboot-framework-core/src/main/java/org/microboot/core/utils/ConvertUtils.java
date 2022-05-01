package org.microboot.core.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.beans.BeanMap;

import java.util.List;
import java.util.Map;

/**
 * @author 胡鹏
 */
public class ConvertUtils {

    public static String map2Json(Map<String, ?> map) {
        if (MapUtils.isEmpty(map)) {
            return null;
        }
        return JSONObject.toJSONString(map, SerializerFeature.WriteMapNullValue);
    }

    public static Map<String, Object> json2Map(String json) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        return JSON.parseObject(json, Feature.OrderedField);
    }

    public static String listMap2Json(List<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return JSONObject.toJSONString(list, SerializerFeature.WriteMapNullValue);
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> json2ListMap(String json) {
        List<Map<String, Object>> list = Lists.newArrayList();
        return ConvertUtils.json2Object(json, list.getClass());
    }

    public static String listStr2Json(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return JSONObject.toJSONString(list, SerializerFeature.WriteMapNullValue);
    }

    @SuppressWarnings("unchecked")
    public static List<String> json2ListStr(String json) {
        List<String> list = Lists.newArrayList();
        return ConvertUtils.json2Object(json, list.getClass());
    }

    public static <T> T json2Object(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        return JSON.parseObject(json, clazz);
    }

    public static String object2Json(Object obj) {
        if (obj == null) {
            return null;
        }
        return JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue);
    }

    public static byte[] object2Bytes(Object obj) {
        if (obj == null) {
            return null;
        }
        return JSON.toJSONBytes(obj, SerializerFeature.WriteMapNullValue);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> bean2Map(Object javaBean) {
        Map<String, Object> parameters = Maps.newHashMap();
        if (javaBean != null) {
            parameters.putAll(BeanMap.create(javaBean));
        }
        return parameters;
    }

    public static List<String[]> json2kv(String json) {
        List<String[]> list = Lists.newArrayList();
        if (StringUtils.isNotBlank(json)) {
            Map<String, Object> map = ConvertUtils.json2Map(json);
            for (String key : map.keySet()) {
                String[] kv = new String[2];
                kv[0] = key;
                kv[1] = MapUtils.getString(map, key);
                list.add(kv);
            }
        }
        return list;
    }

    public static Map<String, Object> uriVars2Map(String uriVars) {
        if (StringUtils.isBlank(uriVars)) {
            return null;
        }
        Map<String, Object> map = Maps.newHashMap();
        if (StringUtils.contains(uriVars, "&")) {
            String[] arr = StringUtils.split(uriVars, "&");
            for (String kv : arr) {
                analysis(map, kv);
            }
        } else {
            analysis(map, uriVars);
        }
        return map;
    }

    private static void analysis(Map<String, Object> map, String kv) {
        String[] kvs = StringUtils.split(kv, "=");
        if (kvs.length != 2) {
            return;
        }
        map.put(kvs[0], kvs[1]);
    }
}
