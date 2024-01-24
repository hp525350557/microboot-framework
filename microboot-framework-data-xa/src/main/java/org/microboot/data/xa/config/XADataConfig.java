package org.microboot.data.xa.config;

import org.microboot.core.constant.Constant;
import org.microboot.data.func.XADataSourceFactoryFunc;
import org.microboot.data.xa.bean.XADataSourceFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * @author 胡鹏
 */
@Configuration
@DependsOn(Constant.APPLICATION_CONTEXT_HOLDER)
public class XADataConfig {

    /**
     * 初始化XADataSourceFactoryBean
     *
     * @return
     */
    @Bean(name = "org.microboot.data.func.XADataSourceFactoryFunc")
    @ConditionalOnMissingBean(name = "org.microboot.data.func.XADataSourceFactoryFunc")
    public XADataSourceFactoryFunc initXADataSourceFactoryFunc() {
        return new XADataSourceFactoryBean();
    }
}