package org.microboot.web.config;

import org.microboot.core.constant.Constant;
import org.microboot.web.filter.WebFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 胡鹏
 */
@Configuration
@DependsOn(Constant.APPLICATION_CONTEXT_HOLDER)
public class WebConfig implements WebMvcConfigurer {

    /**
     * 匹配后缀名
     *
     * @param configurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        //setUseSuffixPatternMatch 设置是否是后缀模式匹配，如“/user”是否匹配/user.*，默认true即匹配；
        //Spring Boot高版本中setUseSuffixPatternMatch(Boolean suffixPatternMatch)过期，默认值：从true 改为 false
        //configurer.setUseSuffixPatternMatch(false);
        //setUseTrailingSlashMatch 设置是否自动后缀路径模式匹配，如“/user”是否匹配“/user/”，默认true即匹配；
        configurer.setUseTrailingSlashMatch(true);
    }

    /**
     * 初始化ServletRegistrationBean 限制后缀名
     *
     * @param dispatcherServlet
     * @return
     */
    @Bean(name = "org.springframework.boot.web.servlet.ServletRegistrationBean")
    public ServletRegistrationBean<DispatcherServlet> initServletRegistrationBean(DispatcherServlet dispatcherServlet) {
        ServletRegistrationBean<DispatcherServlet> servletServletRegistrationBean = new ServletRegistrationBean<>(dispatcherServlet);
        servletServletRegistrationBean.addUrlMappings("/", "*.html");
        return servletServletRegistrationBean;
    }

    /**
     * 初始化FilterRegistrationBean，注册过滤器
     *
     * @return
     */
    @Bean(name = "org.springframework.boot.web.servlet.FilterRegistrationBean<WebFilter>")
    public FilterRegistrationBean<WebFilter> initFilterRegistrationBean() {
        FilterRegistrationBean<WebFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new WebFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setName("WebFilter");
        filterRegistrationBean.setOrder(0);
        return filterRegistrationBean;
    }
}
