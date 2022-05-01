package org.microboot.cache.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author 胡鹏
 */
@Configuration
@ImportResource("${cache.memcached.xml}")
@ConditionalOnProperty(name = "cache.memcached.using", havingValue = "true")
public class MemcachedConfig {
}