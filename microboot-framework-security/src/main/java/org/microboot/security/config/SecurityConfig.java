package org.microboot.security.config;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.microboot.core.constant.Constant;
import org.microboot.security.oauth2.OAuth2Filter;
import org.microboot.security.oauth2.OAuth2Realm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 胡鹏
 */
@Configuration
@DependsOn(Constant.APPLICATION_CONTEXT_HOLDER)
public class SecurityConfig {

    private final boolean DELETE_INVALID_SESSIONS = false;
    private final boolean SESSION_VALIDATION_SCHEDULER_ENABLED = false;
    private final boolean SESSION_ID_URL_REWRITING_ENABLED = false;
    private final boolean SESSION_ID_COOKIE_ENABLED = false;
    private final long SESSION_VALIDATION_INTERVAL = 3_600_000;
    private final long GLOBAL_SESSION_TIMEOUT = 1_800_000;

    private final String OAUTH_URL = "/**";

    private final boolean CACHING_ENABLED = false;
    private final boolean AUTHENTICATION_CACHING_ENABLED = false;
    private final boolean AUTHORIZATION_CACHING_ENABLED = false;

    @Bean(name = "org.microboot.security.oauth2.OAuth2Realm")
    public OAuth2Realm initOAuth2Realm() {
        OAuth2Realm oauth2Realm = new OAuth2Realm();
        //是否开启shiro的缓存
        oauth2Realm.setCachingEnabled(CACHING_ENABLED);
        //是否开启认证缓存
        oauth2Realm.setAuthenticationCachingEnabled(AUTHENTICATION_CACHING_ENABLED);
        //是否开启授权缓存
        oauth2Realm.setAuthorizationCachingEnabled(AUTHORIZATION_CACHING_ENABLED);
        return oauth2Realm;
    }

    @Bean(name = "org.apache.shiro.session.mgt.SessionManager")
    public SessionManager initSessionManager(Environment environment) {
        //本框架由于使用无状态登录，所以要禁用session，下面的属性全都默认设置为false了
        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
        //是否开启删除无效的session对象，缺省值为true
        defaultWebSessionManager.setDeleteInvalidSessions(environment.getProperty("shiro.sessionManager.deleteInvalidSessions") == null
                ? DELETE_INVALID_SESSIONS : Boolean.parseBoolean(environment.getProperty("shiro.sessionManager.deleteInvalidSessions")));
        //是否开启定时调度器进行检测过期session，缺省值为true
        defaultWebSessionManager.setSessionValidationSchedulerEnabled(environment.getProperty("shiro.sessionManager.sessionValidationSchedulerEnabled") == null
                ? SESSION_VALIDATION_SCHEDULER_ENABLED : Boolean.parseBoolean(environment.getProperty("shiro.sessionManager.sessionValidationSchedulerEnabled")));
        //是否取消url后面的JSESSIONID，缺省值为true
        defaultWebSessionManager.setSessionIdUrlRewritingEnabled(environment.getProperty("shiro.sessionManager.sessionIdUrlRewritingEnabled") == null
                ? SESSION_ID_URL_REWRITING_ENABLED : Boolean.parseBoolean(environment.getProperty("shiro.sessionManager.sessionIdUrlRewritingEnabled")));
        //是否将shiro的sessionId放在cookie中，缺省值为true
        defaultWebSessionManager.setSessionIdCookieEnabled(environment.getProperty("shiro.sessionManager.sessionIdCookieEnabled") == null
                ? SESSION_ID_COOKIE_ENABLED : Boolean.parseBoolean(environment.getProperty("shiro.sessionManager.sessionIdCookieEnabled")));
        //定时调度器进行检测过期session的扫描时间，缺省值1个小时
        defaultWebSessionManager.setSessionValidationInterval(environment.getProperty("shiro.sessionManager.sessionValidationInterval") == null
                ? SESSION_VALIDATION_INTERVAL : Long.parseLong(environment.getProperty("shiro.sessionManager.sessionValidationInterval")));
        //全局会话超时时间（单位毫秒），缺省值为30分钟
        defaultWebSessionManager.setGlobalSessionTimeout(environment.getProperty("shiro.sessionManager.globalSessionTimeout") == null
                ? GLOBAL_SESSION_TIMEOUT : Long.parseLong(environment.getProperty("shiro.sessionManager.globalSessionTimeout")));
        return defaultWebSessionManager;
    }

    @Bean(name = "org.apache.shiro.mgt.SecurityManager")
    public SecurityManager initSecurityManager(OAuth2Realm oauth2Realm, SessionManager sessionManager) {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(oauth2Realm);
        defaultWebSecurityManager.setSessionManager(sessionManager);
        defaultWebSecurityManager.setRememberMeManager(null);
        return defaultWebSecurityManager;
    }

    @Bean(name = "org.apache.shiro.spring.web.ShiroFilterFactoryBean")
    public ShiroFilterFactoryBean initShiroFilterFactoryBean(SecurityManager securityManager, Environment environment) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //设置securityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //设置Filter
        Map<String, String> filterMap = new LinkedHashMap<>();
        Map<String, Filter> filters = Maps.newHashMap();
        filters.put("oauth2", new OAuth2Filter());
        shiroFilterFactoryBean.setFilters(filters);
        String anonPaths = environment.getProperty("shiro.anon");
        if (StringUtils.isNotBlank(anonPaths)) {
            String[] anonPathArray = StringUtils.split(anonPaths, ",");
            for (String anonPath : anonPathArray) {
                filterMap.put(anonPath, "anon");
            }
        }
        filterMap.put(OAUTH_URL, "oauth2");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        return shiroFilterFactoryBean;
    }

    @Bean(name = "org.apache.shiro.spring.LifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean(name = "org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor")
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
