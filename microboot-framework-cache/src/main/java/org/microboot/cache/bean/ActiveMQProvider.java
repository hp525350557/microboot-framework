package org.microboot.cache.bean;

import org.apache.commons.lang3.StringUtils;
import org.microboot.cache.entity.CacheMessage;
import org.microboot.cache.func.MQProviderFunc;
import org.microboot.core.bean.ApplicationContextHolder;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.MapMessage;

/**
 * @author 胡鹏
 * microboot框架Local缓存间通信默认使用的是ActiveMQ
 * 开发者可以实现MQProviderFunc，对接自己的MQ
 */
public class ActiveMQProvider implements MQProviderFunc {

    /**
     * 向默认队列发送消息
     * JmsTemplate交由SpringBoot自动装配，不再手动创建
     *
     * @param cacheMessage
     */
    @Override
    public void publish(CacheMessage cacheMessage) {
        String uniqueId = cacheMessage.getUniqueId();
        Object key = cacheMessage.getKey();
        String md5 = cacheMessage.getMd5();
        ApplicationContextHolder.getBean(JmsTemplate.class).send(session -> {
            MapMessage mapMessage = session.createMapMessage();
            mapMessage.setString("uniqueId", uniqueId);
            if (key != null) {
                mapMessage.setObject("key", key);
            }
            if (StringUtils.isNotBlank(md5)) {
                mapMessage.setString("md5", md5);
            }
            return mapMessage;
        });
    }
}