package org.microboot.core.config;

import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.bean.CacheHolder;
import org.microboot.core.bean.DefaultSyncFuncHolder;
import org.microboot.core.constant.Constant;
import org.microboot.core.func.SyncFunc;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 胡鹏
 */
@Configuration
public class CoreConfig {

    /**
     * spring 容器Bean
     *
     * @return
     */
    @Bean(name = Constant.APPLICATION_CONTEXT_HOLDER)
    public ApplicationContextHolder initApplicationContextHolder() {
        return new ApplicationContextHolder();
    }

    /**
     * 缓存
     *
     * @return
     */
    @Bean(name = Constant.CACHE_HOLDER)
    public CacheHolder initCacheHolder() {
        return new CacheHolder();
    }

    /**
     * SyncFunc初始化
     *
     * @return
     */
    @Bean(name = "org.microboot.core.func.SyncFunc")
    @ConditionalOnMissingBean(name = Constant.SYNC_FUNC_HOLDER)
    public SyncFunc initSyncFunc() {
        return new DefaultSyncFuncHolder();
    }
}
