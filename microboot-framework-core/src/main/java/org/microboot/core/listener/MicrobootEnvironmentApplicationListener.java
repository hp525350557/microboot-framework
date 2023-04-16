package org.microboot.core.listener;

import org.microboot.core.listener.propertySources.RandomOncePropertySource;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author 胡鹏
 * <p>
 * 监听器可以按顺序监听以下事件：
 * ApplicationStartingEvent.class > ApplicationEnvironmentPreparedEvent.class >
 * ApplicationContextInitializedEvent.class > ApplicationPreparedEvent.class >
 * ApplicationStartedEvent.class > ApplicationReadyEvent.class > ApplicationFailedEvent.class
 * <p>
 * SpringBoot通过监听器和事件做了很多处理工作
 * SpringBoot启动的时候，会在不同的位置加载合适的监听器，并指定处理相应的事件
 * SpringBoot中监听器排序序号越小，在监听器列表中越靠前
 * SpringBoot中各个事件都有对应的监听器，且大部分监听器的排序序号都以Ordered.HIGHEST_PRECEDENCE为基础进行计算
 * 当我们的监听器需要在对应的事件位置执行时，最好排在相同事件对应的SpringBoot监听器之后执行
 *
 * supportsSourceType方法于判断事件发布的来源，当它返回true&supportsEventType同样返回true时，才会调用该监听内的onApplicationEvent方法
 * 通常默认会直接返回true，Spring官方监听器源码默认也是返回true，也就是说事件源的类型通常对于判断匹配的监听器没有意义
 * 举例：
 * @Override
 * public boolean supportsSourceType(Class<?> sourceType) {
 *     return sourceType == UserService.class;
 * }
 * 表示只有在UserService内发布的指定事件时，这个监听器才会生效
 */
public class MicrobootEnvironmentApplicationListener implements GenericApplicationListener {

    //本监听器是为了给配置文件增加一些占位符扩展，所以需要在ConfigFileApplicationListener之后执行
    private static final int DEFAULT_ORDER = ConfigFileApplicationListener.DEFAULT_ORDER + 1;

    private static final Class<?>[] EVENT_TYPES = {
            ApplicationEnvironmentPreparedEvent.class
    };

    /**
     * 判断发布的事件类型
     * 该方法返回true&supportsSourceType同样返回true时，才会调用该监听内的onApplicationEvent方法
     *
     * @param eventType
     * @return
     */
    @Override
    public boolean supportsEventType(ResolvableType eventType) {
        return isAssignableFrom(eventType.getRawClass(), EVENT_TYPES);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        //判断事件是否是ApplicationEnvironmentPreparedEvent
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            ConfigurableEnvironment environment = ((ApplicationEnvironmentPreparedEvent) event).getEnvironment();
            if (environment == null) {
                return;
            }
            environment.getPropertySources().addLast(new RandomOncePropertySource());
        }
    }

    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }

    private boolean isAssignableFrom(Class<?> type, Class<?>... supportedTypes) {
        if (type != null) {
            for (Class<?> supportedType : supportedTypes) {
                if (supportedType.isAssignableFrom(type)) {
                    return true;
                }
            }
        }
        return false;
    }
}
