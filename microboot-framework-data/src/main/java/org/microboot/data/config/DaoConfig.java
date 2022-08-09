package org.microboot.data.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Maps;
import freemarker.cache.MruCacheStorage;
import freemarker.ext.beans.BeansWrapperBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.constant.Constant;
import org.microboot.data.aspect.ClearThreadLocalAspect;
import org.microboot.data.basedao.BaseDao;
import org.microboot.data.factory.DataSourceFactory;
import org.microboot.data.resolver.TemplateResolver;
import org.microboot.data.runner.StartRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author 胡鹏
 */
@Configuration
@DependsOn(Constant.APPLICATION_CONTEXT_HOLDER)
public class DaoConfig {

    @Value("${datasource.macro:}")
    private String macro;

    /**
     * 初始化ClearThreadLocalAspect
     *
     * @return
     */
    @Bean(name = "org.microboot.data.aspect.ClearThreadLocalAspect")
    public ClearThreadLocalAspect initClearThreadLocalAspect() {
        return new ClearThreadLocalAspect();
    }

    /**
     * 初始化DataSourceFactory
     *
     * @return
     */
    @Bean(name = "org.microboot.data.factory.DataSourceFactory")
    public DataSourceFactory initDataSourceFactory() {
        return new DataSourceFactory();
    }

    /**
     * 初始化DruidDataSource（主库）
     *
     * @return
     */
    @Bean(name = Constant.MASTER_DATA_SOURCE)
    public DruidDataSource initMasterDataSource() {
        Map<String, Object> master = ApplicationContextHolder.getBean(DataSourceFactory.class).getMaster();
        return ApplicationContextHolder.getBean(DataSourceFactory.class).createDataSource(master);
    }

    /**
     * 定义默认的事务管理器
     * 不手动创建，SpringBoot会自动帮我们创建，但是不会添加@Primary
     * 会导致@Transaction找不到默认事务管理器
     *
     * @return
     */
    @Bean
    @Primary
    public PlatformTransactionManager transactionManager() {
        DruidDataSource druidDataSource = ApplicationContextHolder.getBean(Constant.MASTER_DATA_SOURCE, DruidDataSource.class);
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(druidDataSource);
        dataSourceTransactionManager.setNestedTransactionAllowed(true);
        return dataSourceTransactionManager;
    }

    /**
     * 初始化NamedParameterJdbcTemplate（主库）
     *
     * @return
     */
    @Bean(name = Constant.MASTER_JDBC_TEMPLATE)
    public NamedParameterJdbcTemplate initMasterJdbcTemplate() {
        DruidDataSource druidDataSource = ApplicationContextHolder.getBean(Constant.MASTER_DATA_SOURCE, DruidDataSource.class);
        return new NamedParameterJdbcTemplate(druidDataSource);
    }

    /**
     * 初始化Map<String, DruidDataSource>（从库）
     *
     * @return
     */
    @Bean(value = Constant.SLAVES_DATA_SOURCE)
    public Map<String, DruidDataSource> initSlavesDataSource() {
        Map<String, DruidDataSource> dataSourceMap = Maps.newHashMap();
        List<Map<String, Object>> slaves = ApplicationContextHolder.getBean(DataSourceFactory.class).getSlaves();
        if (CollectionUtils.isEmpty(slaves)) {
            //如果未定义从库，则主从用同一个数据源
            DruidDataSource druidDataSource = ApplicationContextHolder.getBean(Constant.MASTER_DATA_SOURCE, DruidDataSource.class);
            putDataSourceMap(dataSourceMap, druidDataSource);
        } else {
            //如果定义了从库，则主从分离，主数据库用来写，从数据库用来读
            for (Map<String, Object> slave : slaves) {
                DruidDataSource druidDataSource = ApplicationContextHolder.getBean(DataSourceFactory.class).createDataSource(slave);
                putDataSourceMap(dataSourceMap, druidDataSource);
            }
        }
        return dataSourceMap;
    }

    /**
     * 初始化Map<String, DruidDataSource>（其他库）
     *
     * @return
     */
    @Bean(value = Constant.OTHERS_DATA_SOURCE)
    public Map<String, DruidDataSource> initOthersDataSource() {
        Map<String, DruidDataSource> dataSourceMap = Maps.newHashMap();
        List<Map<String, Object>> others = ApplicationContextHolder.getBean(DataSourceFactory.class).getOthers();
        if (CollectionUtils.isEmpty(others)) {
            //如果未定义其他库，则返回空的dataSourceMap
            return dataSourceMap;
        } else {
            //如果定义了其他库，则构建其他库连接池
            for (Map<String, Object> other : others) {
                //连接池名
                DruidDataSource druidDataSource = ApplicationContextHolder.getBean(DataSourceFactory.class).createDataSource(other);
                putDataSourceMap(dataSourceMap, druidDataSource);
            }
        }

        //动态构建事务管理器
        this.dynamicTransactionManager(dataSourceMap);

        return dataSourceMap;
    }

    /**
     * 初始化StartRunner
     *
     * @return
     */
    @Bean(name = "org.microboot.data.runner.StartRunner")
    public StartRunner initStartRunner() {
        return new StartRunner();
    }

    /**
     * 初始化freemarker.template.Configuration
     *
     * @return
     * @throws Exception
     */
    @Bean(name = Constant.FREEMARKER_CONFIGURATION)
    public freemarker.template.Configuration initConfiguration() throws Exception {
        freemarker.template.Configuration configuration = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_24);
        configuration.setCacheStorage(new MruCacheStorage(0, Integer.MAX_VALUE));
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("beansWrapperFn", new BeansWrapperBuilder(freemarker.template.Configuration.VERSION_2_3_24).build());
        configuration.setSharedVariables(variables);
		/*
		 1、autoImport是用key-value的方式匹配需要自动加载的模板，即：使用一个变量指向一个模板
		 2、传统方式：
		        freemarker加载模板位置是在classpath:/templates/，查看FreeMarkerProperties这个类
		        autoImport的value是freemarker模板文件的相对路径，比如：
		            classpath:/templates/sql/macro.sql -> autoImport.put("macro", "sql/macro.sql");
		 3、microboot框架改变了freemarker模板的加载位置（兼容打jar包后读取模板），查看TemplateResolver这个类
            所以此时autoImport的value是freemarker模板文件加载到内存后的key值，如下：
            autoImport.put("macro", "macro"); value值的macro是模板加载时的templateKey
		 */
        if (StringUtils.isNotBlank(macro)) {
            Map<String, Object> autoImport = Maps.newHashMap();
            autoImport.put("macro", macro);
            configuration.setAutoImports(autoImport);
        }
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
        configuration.setSettings(freemarkerSettings);
        return configuration;
    }

    /**
     * 初始化TemplateResolver
     *
     * @return
     */
    @Bean(name = "org.microboot.data.resolver.TemplateResolver")
    public TemplateResolver initTemplateResolver() {
        return new TemplateResolver();
    }

    /**
     * 初始化BaseDao
     *
     * @return
     */
    @Bean(name = "org.microboot.data.basedao.BaseDao")
    public BaseDao initBaseDao() {
        return new BaseDao();
    }

    /**
     * 组装dataSourceMap
     *
     * @param dataSourceMap
     * @param druidDataSource
     */
    private void putDataSourceMap(Map<String, DruidDataSource> dataSourceMap, DruidDataSource druidDataSource) {
        String name = druidDataSource.getName();
        if (StringUtils.isBlank(name)) {
            return;
        }
        dataSourceMap.put(name, druidDataSource);
    }

    /**
     * 动态事务管理器（其他库）
     *
     * @param dataSourceMap
     * @return
     */
    private void dynamicTransactionManager(Map<String, DruidDataSource> dataSourceMap) {
        if (MapUtils.isEmpty(dataSourceMap)) {
            return;
        }

        Set<String> dataSourceNames = dataSourceMap.keySet();

        //将applicationContext转换为ConfigurableApplicationContext
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) ApplicationContextHolder.getApplicationContext();
        //获取bean工厂并转换为DefaultListableBeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();

        for (String dataSourceName : dataSourceNames) {
            DruidDataSource druidDataSource = dataSourceMap.get(dataSourceName);
            if (druidDataSource == null) {
                continue;
            }
            //通过BeanDefinitionBuilder创建bean定义
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceTransactionManager.class);
            //设置bean属性
            beanDefinitionBuilder.addPropertyValue("dataSource", druidDataSource);
            //注册bean
            defaultListableBeanFactory.registerBeanDefinition(dataSourceName + "&transactionManager", beanDefinitionBuilder.getRawBeanDefinition());
        }
    }
}
