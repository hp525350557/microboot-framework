package org.microboot.view.config;

import com.google.common.collect.Maps;
import freemarker.ext.beans.BeansWrapperBuilder;
import org.microboot.core.constant.Constant;
import org.microboot.view.bean.FreeMarkerConfigurerExtender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

/**
 * @author 胡鹏
 */
@Configuration
@DependsOn(Constant.APPLICATION_CONTEXT_HOLDER)
public class ViewConfig {

    @Value("${custom.resources.views.location:}")
    private String CUSTOM_RESOURCES_VIEWS_LOCATION;

    /**
     * 初始化FreeMarkerConfigurerExtender
     *
     * @return
     */
    @Bean(name = "org.microboot.view.bean.FreeMarkerConfigurerExtender")
    public FreeMarkerConfigurerExtender initFreeMarkerConfigurerExtender() {
        FreeMarkerConfigurerExtender configurer = new FreeMarkerConfigurerExtender();
        configurer.setTemplateLoaderPaths(CUSTOM_RESOURCES_VIEWS_LOCATION);
        configurer.setDefaultEncoding(StandardCharsets.UTF_8.name());
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("beansWrapperFn", new BeansWrapperBuilder(freemarker.template.Configuration.VERSION_2_3_24).build());
        configurer.setFreemarkerVariables(variables);
        Properties freemarkerSettings = new Properties();
        freemarkerSettings.setProperty("template_update_delay", Constant.CODE_5);
        freemarkerSettings.setProperty("default_encoding", StandardCharsets.UTF_8.name());
        freemarkerSettings.setProperty("output_encoding", StandardCharsets.UTF_8.name());
        freemarkerSettings.setProperty("url_escaping_charset", StandardCharsets.UTF_8.name());
        freemarkerSettings.setProperty("datetime_format", "yyyy-MM-dd HH:mm:ss");
        freemarkerSettings.setProperty("date_format", "yyyy-MM-dd");
        freemarkerSettings.setProperty("time_format", "HH:mm:ss");
        freemarkerSettings.setProperty("number_format", "#.##");
        freemarkerSettings.setProperty("tag_syntax", "auto_detect");
        freemarkerSettings.setProperty("boolean_format", "true,false");
        freemarkerSettings.setProperty("whitespace_stripping", "true");
        configurer.setFreemarkerSettings(freemarkerSettings);
        return configurer;
    }

    /**
     * 初始化FreeMarkerViewResolver
     * <p>
     * 如果使用自动装配的方式加载配置类，则不能使用JavaBean的方式自定义FreeMarkerViewResolver
     * <p>
     * 另外，如果使用扫描的方式，最好指定FreeMarkerViewResolver的beanName = freeMarkerViewResolver
     * 因为FreeMarker默认的FreeMarkerViewResolver创建Bean时的条件是@ConditionalOnMissingBean(name = "freeMarkerViewResolver")
     *
     * @return
     */
    /*
    @Bean(name = "org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver")
    public FreeMarkerViewResolver initFreeMarkerViewResolver() {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
        //resolver.setPrefix("");//设置地址前缀
        resolver.setSuffix(".html");//设置后缀
        resolver.setContentType("text/html;charset=UTF-8");
        resolver.setRequestContextAttribute("request");
        resolver.setCache(false);
        return resolver;
    }
    */
}
