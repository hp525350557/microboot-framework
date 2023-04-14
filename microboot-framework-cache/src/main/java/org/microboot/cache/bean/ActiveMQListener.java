package org.microboot.cache.bean;

import org.microboot.cache.entity.CacheMessage;
import org.microboot.cache.func.MQListenerFunc;
import org.springframework.jms.annotation.JmsListener;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * @author 胡鹏
 * microboot框架Local缓存间通信默认使用的是ActiveMQ
 * 开发者可以实现MQListenerFunc，对接自己的MQ
 */
public class ActiveMQListener implements MessageListener, MQListenerFunc {

    /**
     * 接收默认队列消息
     *
     * 1、SpringBoot自动装配DefaultJmsListenerContainerFactory对象，beanName = jmsListenerContainerFactory
     * 创建DefaultJmsListenerContainerFactory对象依赖javax.jms.ConnectionFactory
     *
     * 2、javax.jms.ConnectionFactory交由SpringBoot自动装配，不再手动创建ActiveMQConnectionFactory
     *
     * 3、destination通过SpEL表达式从Spring上下文容器中拿到ActiveMQTopic的topicName，确保监听的队列与ActiveMQTopic保持一致
     *
     * @param message
     */
    @Override
    @JmsListener(destination = "#{@activeMQTopic.topicName}", containerFactory = "jmsListenerContainerFactory")
    public void onMessage(Message message) {
        clearLocalCaches(() -> {
            MapMessage mm = (MapMessage) message;
            String uniqueId = this.get(() -> mm.getString("uniqueId"));
            Object key = this.get(() -> mm.getObject("key"));
            String md5 = this.get(() -> mm.getString("md5"));

            CacheMessage cacheMessage = new CacheMessage();
            cacheMessage.setUniqueId(uniqueId);
            cacheMessage.setKey(key);
            cacheMessage.setMd5(md5);

            return cacheMessage;
        });
    }
}