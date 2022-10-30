package org.microboot.cache.bean;

import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.utils.ConvertUtils;
import org.microboot.core.utils.CryptoUtils;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.MapMessage;

/**
 * @author 胡鹏
 */
public class CacheMQTopicProvider {

    /**
     * * 向默认队列发送消息
     * JmsTemplate交由SpringBoot自动装配，不再手动创建
     *
     * @param uniqueId
     * @param key
     * @param value
     */
    public void publish(final String uniqueId, final Object key, final Object value) {
        if (ApplicationContextHolder.getBean(JmsTemplate.class).getDefaultDestination() == null) {
            return;
        }
        ApplicationContextHolder.getBean(JmsTemplate.class).send(session -> {
            MapMessage mapMessage = session.createMapMessage();
            mapMessage.setString("uniqueId", uniqueId);
            if (key != null) {
                mapMessage.setObject("key", key);
            }
            if (value != null) {
                mapMessage.setString("md5", CryptoUtils.md5Hex(ConvertUtils.object2Bytes(value)));
            }
            return mapMessage;
        });
    }
}
