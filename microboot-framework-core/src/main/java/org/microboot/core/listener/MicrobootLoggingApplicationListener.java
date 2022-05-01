package org.microboot.core.listener;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.microboot.core.utils.ProcessUtil;
import org.slf4j.MDC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.Map;

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
 */
public class MicrobootLoggingApplicationListener implements GenericApplicationListener {

    //本监听器需要将配置文件中日志相关配置的${pid}占位符替换成进程ID，所以需要在ConfigFileApplicationListener之后，LoggingApplicationListener之前执行
    private static final int DEFAULT_ORDER = ConfigFileApplicationListener.DEFAULT_ORDER + 1;

    private static final Class<?>[] EVENT_TYPES = {
            ApplicationEnvironmentPreparedEvent.class
    };

    private static final Class<?>[] SOURCE_TYPES = {
            SpringApplication.class, ApplicationContext.class
    };

    @Override
    public boolean supportsEventType(ResolvableType eventType) {
        return isAssignableFrom(eventType.getRawClass(), EVENT_TYPES);
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return isAssignableFrom(sourceType, SOURCE_TYPES);
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
            MutablePropertySources propertySources = environment.getPropertySources();
            //log4j2配置
            for (PropertySource<?> propertySource : propertySources) {
                Object source = propertySource.getSource();
                if (!(source instanceof Map)) {
                    continue;
                }
                Map<String, Object> map = (Map<String, Object>) source;
                for (String key : map.keySet()) {
                    if (!StringUtils.contains(key, "custom.logging")) {
                        continue;
                    }
                    String value = MapUtils.getString(map, key);
                    if (StringUtils.isBlank(value)) {
                        continue;
                    }
                    String newKey = StringUtils.replace(key, "custom.logging.", "");
                    if (StringUtils.containsIgnoreCase(value, "${pid}")) {
                        value = StringUtils.replace(value, "${pid}", ProcessUtil.getProcessId());
                    }
                    MDC.put(newKey, value);
                }
            }
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