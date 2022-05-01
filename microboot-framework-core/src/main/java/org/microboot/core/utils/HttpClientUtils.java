package org.microboot.core.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * @author 胡鹏
 */
public class HttpClientUtils {

    private static final Logger logger = LogManager.getLogger(HttpClientUtils.class);

    public static String post(String url, int retry, int maxRetry, Map<String, Object> headers, Map<String, Object> parameters) {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        String result = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            client = HttpClientBuilder.create().build();
            if (MapUtils.isNotEmpty(headers)) {
                for (String key : headers.keySet()) {
                    String value = MapUtils.getString(headers, key, "");
                    httpPost.setHeader(key, value);
                }
            }
            if (MapUtils.isNotEmpty(parameters)) {
                List<NameValuePair> basicNameValuePairList = getNameValuePairsList(parameters);
                if (CollectionUtils.isNotEmpty(basicNameValuePairList)) {
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(basicNameValuePairList, Consts.UTF_8);
                    httpPost.setEntity(entity);
                }
            }
            RequestConfig requestConfig = getRequestConfig();
            httpPost.setConfig(requestConfig);
            response = client.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            result = getResult(response);
            logger.info("http请求url：" + url);
            logger.info("http请求headers：" + headers);
            logger.info("http请求parameters：" + parameters);
            logger.info("http请求statusCode：" + statusCode);
            if (statusCode != HttpStatus.SC_OK) {
                logger.info(result);
            }
            return result;
        } catch (Throwable e) {
            if (retry <= maxRetry) {
                retry++;
                logger.info("请求url：" + url + "异常，第" + retry + "次重试");
                result = HttpClientUtils.post(url, retry, maxRetry, headers, parameters);
            }
            LoggerUtils.error(logger, e);
        } finally {
            close(client, response);
        }
        return result;
    }

    public static String postRaw(String url, int retry, int maxRetry, Map<String, Object> headers, Map<String, Object> parameters) {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        String result = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            client = HttpClientBuilder.create().build();
            if (MapUtils.isNotEmpty(headers)) {
                for (String key : headers.keySet()) {
                    String value = MapUtils.getString(headers, key, "");
                    httpPost.setHeader(key, value);
                }
            }
            if (MapUtils.isNotEmpty(parameters)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.putAll(parameters);
                StringEntity entity = new StringEntity(jsonObject.toString(), Consts.UTF_8);
                entity.setContentEncoding(Consts.UTF_8.name());
                entity.setContentType(ContentType.APPLICATION_JSON.toString());
                httpPost.setEntity(entity);
            }
            RequestConfig requestConfig = getRequestConfig();
            httpPost.setConfig(requestConfig);
            response = client.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            result = getResult(response);
            logger.info("http请求url：" + url);
            logger.info("http请求headers：" + headers);
            logger.info("http请求parameters：" + parameters);
            logger.info("http请求statusCode：" + statusCode);
            if (statusCode != HttpStatus.SC_OK) {
                logger.info(result);
            }
            return result;
        } catch (Throwable e) {
            if (retry <= maxRetry) {
                retry++;
                logger.info("请求url：" + url + "异常，第" + retry + "次重试");
                result = HttpClientUtils.postRaw(url, retry, maxRetry, headers, parameters);
            }
            LoggerUtils.error(logger, e);
        } finally {
            close(client, response);
        }
        return result;
    }

    public static String get(String url, int retry, int maxRetry, Map<String, Object> headers, Map<String, Object> parameters) {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        String result = null;
        try {
            URIBuilder uri = new URIBuilder(url, Consts.UTF_8);
            client = HttpClientBuilder.create().build();
            if (MapUtils.isNotEmpty(parameters)) {
                List<NameValuePair> basicNameValuePairList = getNameValuePairsList(parameters);
                if (CollectionUtils.isNotEmpty(basicNameValuePairList)) {
                    uri.setParameters(basicNameValuePairList);
                }
            }
            HttpGet httpGet = new HttpGet(uri.build());
            if (MapUtils.isNotEmpty(headers)) {
                for (String key : headers.keySet()) {
                    String value = MapUtils.getString(headers, key, "");
                    httpGet.setHeader(key, value);
                }
            }
            RequestConfig requestConfig = getRequestConfig();
            httpGet.setConfig(requestConfig);
            response = client.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            result = getResult(response);
            logger.info("http请求url：" + url);
            logger.info("http请求headers：" + headers);
            logger.info("http请求parameters：" + parameters);
            logger.info("http请求statusCode：" + statusCode);
            if (statusCode != HttpStatus.SC_OK) {
                logger.info(result);
            }
            return result;
        } catch (Throwable e) {
            if (retry <= maxRetry) {
                retry++;
                logger.info("请求url：" + url + "异常，第" + retry + "次重试");
                result = HttpClientUtils.get(url, retry, maxRetry, headers, parameters);
            }
            LoggerUtils.error(logger, e);
        } finally {
            close(client, response);
        }
        return result;
    }

    private static String getResult(CloseableHttpResponse response) throws Exception {
        String result = null;
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            result = EntityUtils.toString(entity, Consts.UTF_8.name());
        }
        return result;
    }

    private static void close(CloseableHttpClient client, CloseableHttpResponse response) {
        if (response != null) {
            try {
                response.close();
            } catch (Throwable e) {
                LoggerUtils.error(logger, e);
            }
        }
        if (client != null) {
            try {
                client.close();
            } catch (Throwable e) {
                LoggerUtils.error(logger, e);
            }
        }
    }

    private static RequestConfig getRequestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(60000)
                .setSocketTimeout(60000)
                .setConnectTimeout(60000)
                .build();
    }

    private static List<NameValuePair> getNameValuePairsList(Map<String, Object> parameters) {
        List<NameValuePair> basicNameValuePairList = Lists.newArrayList();
        for (String key : parameters.keySet()) {
            String value = MapUtils.getString(parameters, key);
            if (StringUtils.isBlank(value)) {
                continue;
            }
            BasicNameValuePair basicNameValuePair = new BasicNameValuePair(key, value);
            basicNameValuePairList.add(basicNameValuePair);
        }
        return basicNameValuePairList;
    }
}
