package org.microboot.logging.config;

import org.microboot.core.constant.Constant;
import org.microboot.logging.aspect.LoggingAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * @author 胡鹏
 */
@Configuration
@DependsOn(Constant.APPLICATION_CONTEXT_HOLDER)
public class LoggingConfig {

    /**
     * 初始化LoggingAspect
     *
     * @return
     */
    @Bean(name = "org.microboot.logging.aspect.LoggingAspect")
    public LoggingAspect initLoggingAspect() {
        return new LoggingAspect();
    }
}
