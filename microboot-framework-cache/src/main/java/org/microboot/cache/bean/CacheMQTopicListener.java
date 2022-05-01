package org.microboot.cache.bean;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.microboot.cache.impl.AbstractLocalCache;
import org.microboot.core.func.Func0;
import org.microboot.core.utils.ConvertUtils;
import org.microboot.core.utils.CryptoUtils;
import org.springframework.cache.Cache;
import org.springframework.jms.annotation.JmsListener;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Set;

/**
 * @author 胡鹏
 */
public class CacheMQTopicListener implements MessageListener {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final Set<AbstractLocalCache> caches;

    public CacheMQTopicListener(Set<AbstractLocalCache> caches) {
        this.caches = caches;
    }

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
        if (CollectionUtils.isEmpty(caches)) {
            return;
        }
        MapMessage mm = (MapMessage) message;
        Integer hashCode = this.get(() -> mm.getInt("hashCode"));
        Object key = this.get(() -> mm.getObject("key"));
        String md5 = this.get(() -> mm.getString("md5"));
        if (hashCode == null || hashCode == 0) {
            return;
        }
        for (AbstractLocalCache cache : caches) {
            int currHashCode = cache.getClass().hashCode();
            //这里判断是否是当前服务的本地缓存，如果是，则不需要重复清除
            if (hashCode == currHashCode) {
                continue;
            }
            if (key == null) {
                cache.clearByMQ();
            } else {
                /*
                    当开启多个服务时，本地缓存通过消息队列的广播来清除被更新的数据，以达到微服务之间本地缓存同步的效果
                    但是@Cacheable注解在获取不到缓存数据时，会调用业务方法，并调用Cache接口的put方法缓存返回值
                    这就造成了@Cacheable也会进入setValue方法，并触发缓存清除事件，这就导致了一个问题：
                    【
                        A服务被调用 -> 检查是否有缓存 -> 否 -> 调用业务方法，并缓存数据，同时触发缓存清除事件 -> B，C等服务的缓存被清除
                        B服务被调用 -> 检查是否有缓存 -> 否 -> 调用业务方法，并缓存数据，同时触发缓存清除事件 -> A，C等服务的缓存被清除
                        C服务被调用 -> 检查是否有缓存 -> 否 -> 调用业务方法，并缓存数据，同时触发缓存清除事件 -> A，B等服务的缓存被清除
                    】
                    这种情况导致@Cacheable标注的方法实际上并没有真正起作用
                    因此在调用setValue方法时需要通过缓存数据的字节数组来判断数据是否真正变化
                    只有数据真的发生变化了的服务，才需要清除
                 */
                Cache.ValueWrapper valueWrapper = cache.get(key);
                if (valueWrapper == null) {
                    continue;
                }
                Object value = cache.get(key).get();
                if (value == null) {
                    continue;
                }
                String currMd5 = CryptoUtils.md5Hex(ConvertUtils.object2Bytes(value));
                if (StringUtils.equals(md5, currMd5)) {
                    continue;
                }
                cache.evictByMQ(key);
            }
        }
    }

    private <T> T get(Func0<T> func) {
        try {
            return func.func();
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        return null;
    }
}