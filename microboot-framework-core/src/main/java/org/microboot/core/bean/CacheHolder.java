package org.microboot.core.bean;

import org.microboot.core.constant.Constant;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

/**
 * @author 胡鹏
 */
public class CacheHolder {

    /**********************************************************操作通用缓存************************************************************/
    @CachePut(value = Constant.CACHE_NAME, key = Constant.CACHE_ROOT_TARGETCLASS_NAME + "#key")
    public Object setAttribute(String key, Object value) {
        return value;
    }

    @CachePut(value = Constant.CACHE_NAME, key = Constant.CACHE_ROOT_TARGETCLASS_NAME + "#key + '&' + #salt")
    public Object setAttribute(String key, Object value, String salt) {
        return value;
    }

    @Cacheable(value = Constant.CACHE_NAME, key = Constant.CACHE_ROOT_TARGETCLASS_NAME + "#key")
    public Object getAttribute(String key) {
        return null;
    }

    @Cacheable(value = Constant.CACHE_NAME, key = Constant.CACHE_ROOT_TARGETCLASS_NAME + "#key + '&' + #salt")
    public Object getAttribute(String key, String salt) {
        return null;
    }

    @CacheEvict(value = Constant.CACHE_NAME, key = Constant.CACHE_ROOT_TARGETCLASS_NAME + "#key")
    public void removeAttribute(String key) {
    }

    @CacheEvict(value = Constant.CACHE_NAME, key = Constant.CACHE_ROOT_TARGETCLASS_NAME + "#key + '&' + #salt")
    public void removeAttribute(String key, String salt) {
    }

    @CachePut(value = Constant.CACHE_NAME, key = "#key")
    public Object put(String key, Object value) {
        return value;
    }

    @CachePut(value = Constant.CACHE_NAME, key = "#key + '&' + #salt")
    public Object put(String key, Object value, String salt) {
        return value;
    }

    @Cacheable(value = Constant.CACHE_NAME, key = "#key")
    public Object get(String key) {
        return null;
    }

    @Cacheable(value = Constant.CACHE_NAME, key = "#key + '&' + #salt")
    public Object get(String key, String salt) {
        return null;
    }

    @CacheEvict(value = Constant.CACHE_NAME, key = "#key")
    public void evict(String key) {
    }

    @CacheEvict(value = Constant.CACHE_NAME, key = "#key + '&' + #salt")
    public void evict(String key, String salt) {
    }

    @CacheEvict(value = Constant.CACHE_NAME, allEntries = true)
    public void clear() {
    }

    /**********************************************************操作本地缓存************************************************************/
    @CachePut(value = Constant.CACHE_LOCAL_NAME, key = Constant.CACHE_ROOT_TARGETCLASS_NAME + "#key")
    public Object setAttributeForLocal(String key, Object value) {
        return value;
    }

    @CachePut(value = Constant.CACHE_LOCAL_NAME, key = Constant.CACHE_ROOT_TARGETCLASS_NAME + "#key + '&' + #salt")
    public Object setAttributeForLocal(String key, Object value, String salt) {
        return value;
    }

    @Cacheable(value = Constant.CACHE_LOCAL_NAME, key = Constant.CACHE_ROOT_TARGETCLASS_NAME + "#key")
    public Object getAttributeForLocal(String key) {
        return null;
    }

    @Cacheable(value = Constant.CACHE_LOCAL_NAME, key = Constant.CACHE_ROOT_TARGETCLASS_NAME + "#key + '&' + #salt")
    public Object getAttributeForLocal(String key, String salt) {
        return null;
    }

    @CacheEvict(value = Constant.CACHE_LOCAL_NAME, key = Constant.CACHE_ROOT_TARGETCLASS_NAME + "#key")
    public void removeAttributeForLocal(String key) {
    }

    @CacheEvict(value = Constant.CACHE_LOCAL_NAME, key = Constant.CACHE_ROOT_TARGETCLASS_NAME + "#key + '&' + #salt")
    public void removeAttributeForLocal(String key, String salt) {
    }

    @CachePut(value = Constant.CACHE_LOCAL_NAME, key = "#key")
    public Object putForLocal(String key, Object value) {
        return value;
    }

    @CachePut(value = Constant.CACHE_LOCAL_NAME, key = "#key + '&' + #salt")
    public Object putForLocal(String key, Object value, String salt) {
        return value;
    }

    @Cacheable(value = Constant.CACHE_LOCAL_NAME, key = "#key")
    public Object getForLocal(String key) {
        return null;
    }

    @Cacheable(value = Constant.CACHE_LOCAL_NAME, key = "#key + '&' + #salt")
    public Object getForLocal(String key, String salt) {
        return null;
    }

    @CacheEvict(value = Constant.CACHE_LOCAL_NAME, key = "#key")
    public void evictForLocal(String key) {
    }

    @CacheEvict(value = Constant.CACHE_LOCAL_NAME, key = "#key + '&' + #salt")
    public void evictForLocal(String key, String salt) {
    }

    @CacheEvict(value = Constant.CACHE_LOCAL_NAME, allEntries = true)
    public void clearForLocal() {
    }

    /**********************************************************操作中央缓存************************************************************/
    @CachePut(value = Constant.CACHE_CENTRAL_NAME, key = Constant.CACHE_ROOT_TARGETCLASS_NAME + "#key")
    public Object setAttributeForCentral(String key, Object value) {
        return value;
    }

    @CachePut(value = Constant.CACHE_CENTRAL_NAME, key = Constant.CACHE_ROOT_TARGETCLASS_NAME + "#key + '&' + #salt")
    public Object setAttributeForCentral(String key, Object value, String salt) {
        return value;
    }

    @Cacheable(value = Constant.CACHE_CENTRAL_NAME, key = Constant.CACHE_ROOT_TARGETCLASS_NAME + "#key")
    public Object getAttributeForCentral(String key) {
        return null;
    }

    @Cacheable(value = Constant.CACHE_CENTRAL_NAME, key = Constant.CACHE_ROOT_TARGETCLASS_NAME + "#key + '&' + #salt")
    public Object getAttributeForCentral(String key, String salt) {
        return null;
    }

    @CacheEvict(value = Constant.CACHE_CENTRAL_NAME, key = Constant.CACHE_ROOT_TARGETCLASS_NAME + "#key")
    public void removeAttributeForCentral(String key) {
    }

    @CacheEvict(value = Constant.CACHE_CENTRAL_NAME, key = Constant.CACHE_ROOT_TARGETCLASS_NAME + "#key + '&' + #salt")
    public void removeAttributeForCentral(String key, String salt) {
    }

    @CachePut(value = Constant.CACHE_CENTRAL_NAME, key = "#key")
    public Object putForCentral(String key, Object value) {
        return value;
    }

    @CachePut(value = Constant.CACHE_CENTRAL_NAME, key = "#key + '&' + #salt")
    public Object putForCentral(String key, Object value, String salt) {
        return value;
    }

    @Cacheable(value = Constant.CACHE_CENTRAL_NAME, key = "#key")
    public Object getForCentral(String key) {
        return null;
    }

    @Cacheable(value = Constant.CACHE_CENTRAL_NAME, key = "#key + '&' + #salt")
    public Object getForCentral(String key, String salt) {
        return null;
    }

    @CacheEvict(value = Constant.CACHE_CENTRAL_NAME, key = "#key")
    public void evictForCentral(String key) {
    }

    @CacheEvict(value = Constant.CACHE_CENTRAL_NAME, key = "#key + '&' + #salt")
    public void evictForCentral(String key, String salt) {
    }

    @CacheEvict(value = Constant.CACHE_CENTRAL_NAME, allEntries = true)
    public void clearForCentral() {
    }
}
