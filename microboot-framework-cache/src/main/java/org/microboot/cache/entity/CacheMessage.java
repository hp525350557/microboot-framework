package org.microboot.cache.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 胡鹏
 */
@Setter
@Getter
public class CacheMessage {

    private String uniqueId;

    private Object key;

    private String md5;
}