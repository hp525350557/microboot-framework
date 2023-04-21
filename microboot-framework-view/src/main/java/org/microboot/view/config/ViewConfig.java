package org.microboot.view.config;

import com.google.common.collect.Maps;
import freemarker.ext.beans.BeansWrapperBuilder;
import org.microboot.core.constant.Constant;
import org.microboot.view.bean.FreeMarkerConfigurerExtender;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.Map;
import java.util.Properties;

/**
 * @author 胡鹏
 */
@Configuration
@DependsOn(Constant.APPLICATION_CONTEXT_HOLDER)
public class ViewConfig {

    @Bean(name = "org.microboot.view.bean.FreeMarkerConfigurerExtender")
    public FreeMarkerConfigurerExtender initFreeMarkerConfigurerExtender(FreeMarkerProperties properties) {
        FreeMarkerConfigurerExtender configurer = new FreeMarkerConfigurerExtender();
        //本来想完全交给SpringBoot去管理，直接在配置文件中写配置信息即可
        //但是测试发现beansWrapperFn会报错，可能是因为不是在构建Bean的时候设置的
        //所以参考SpringBoot的构建方式，手动创建，现在settings属性还是在配置文件中设置
        configurer.setTemplateLoaderPaths(properties.getTemplateLoaderPath());
        configurer.setPreferFileSystemAccess(properties.isPreferFileSystemAccess());
        configurer.setDefaultEncoding(properties.getCharsetName());
        Properties settings = new Properties();
        settings.putAll(properties.getSettings());
        configurer.setFreemarkerSettings(settings);
        //用于整合Java工具类
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("beansWrapperFn", new BeansWrapperBuilder(freemarker.template.Configuration.VERSION_2_3_24).build());
        configurer.setFreemarkerVariables(variables);
        return configurer;
    }
}