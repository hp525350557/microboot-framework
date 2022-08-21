package org.microboot.data.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Maps;
import freemarker.cache.MruCacheStorage;
import freemarker.ext.beans.BeansWrapperBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.constant.Constant;
import org.microboot.data.aspect.ClearThreadLocalAspect;
import org.microboot.data.basedao.BaseDao;
import org.microboot.data.factory.DataSourceFactory;
import org.microboot.data.processor.DataSourcePostProcessor;
import org.microboot.data.resolver.TemplateResolver;
import org.microboot.data.runner.StartRunner;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author 胡鹏
 */
@Configuration
@DependsOn(Constant.APPLICATION_CONTEXT_HOLDER)
public class DaoConfig {

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
     * 初始化DataSource（主库）
     *
     * @param dataSourceFactory
     * @return
     */
    @Primary
    @Bean(name = Constant.MASTER_DATA_SOURCE)
    public DataSource initMasterDataSource(DataSourceFactory dataSourceFactory) {
        Map<String, Object> master = dataSourceFactory.getMaster();
        DruidDataSource dataSource = dataSourceFactory.createDataSource(master);
        AtomikosDataSourceBean atomikosDataSource = dataSourceFactory.createAtomikosDataSourceBean(dataSource);
        if (atomikosDataSource != null) return atomikosDataSource;
        return dataSource;
    }

    /**
     * 初始化NamedParameterJdbcTemplate（主库）
     *
     * @return
     */
    @Bean(name = Constant.MASTER_JDBC_TEMPLATE)
    public NamedParameterJdbcTemplate initMasterJdbcTemplate() {
        DataSource dataSource = ApplicationContextHolder.getBean(Constant.MASTER_DATA_SOURCE, DataSource.class);
        return new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * 初始化Map<String, DataSource>（从库）
     *
     * @param dataSourceFactory
     * @return
     */
    @Bean(value = Constant.SLAVES_DATA_SOURCE)
    public Map<String, DataSource> initSlavesDataSource(DataSourceFactory dataSourceFactory) {
        Map<String, DataSource> dataSourceMap = Maps.newHashMap();
        List<Map<String, Object>> slaves = dataSourceFactory.getSlaves();
        if (CollectionUtils.isEmpty(slaves)) {
            //如果未定义从库，则主从用同一个数据源
            DataSource dataSource = ApplicationContextHolder.getBean(Constant.MASTER_DATA_SOURCE, DataSource.class);
            putDataSourceMap(dataSourceMap, dataSource);
        } else {
            //如果定义了从库，则主从分离，主数据库用来写，从数据库用来读
            for (Map<String, Object> slave : slaves) {
                DruidDataSource dataSource = dataSourceFactory.createDataSource(slave);
                AtomikosDataSourceBean atomikosDataSource = dataSourceFactory.createAtomikosDataSourceBean(dataSource);
                if (atomikosDataSource != null) {
                    putDataSourceMap(dataSourceMap, atomikosDataSource);
                    continue;
                }
                putDataSourceMap(dataSourceMap, dataSource);
            }
        }
        return dataSourceMap;
    }

    /**
     * 初始化Map<String, DataSource>（其他库）
     *
     * @param dataSourceFactory
     * @return
     */
    @Bean(value = Constant.OTHERS_DATA_SOURCE)
    public Map<String, DataSource> initOthersDataSource(DataSourceFactory dataSourceFactory) {
        Map<String, DataSource> dataSourceMap = Maps.newHashMap();
        List<Map<String, Object>> others = dataSourceFactory.getOthers();
        if (CollectionUtils.isEmpty(others)) {
            //如果未定义其他库，则返回空的dataSourceMap
            return dataSourceMap;
        } else {
            //如果定义了其他库，则构建其他库连接池
            for (Map<String, Object> other : others) {
                DruidDataSource dataSource = dataSourceFactory.createDataSource(other);
                AtomikosDataSourceBean atomikosDataSource = dataSourceFactory.createAtomikosDataSourceBean(dataSource);
                if (atomikosDataSource != null) {
                    putDataSourceMap(dataSourceMap, atomikosDataSource);
                    continue;
                }
                putDataSourceMap(dataSourceMap, dataSource);
            }
        }
        return dataSourceMap;
    }

    /**
     * 初始化DataSourcePostProcessor
     *
     * 尝试过的方法：
     * 1、@ConditionalOnProperty，获取不到database.yml中的属性值【本意是不想把enableXA属性和database属性拆开到不同的配置文件中】
     * 2、根据enableXA选择创建不同的事务管理器【JtaTransactionManager或DataSourceTransactionManager】，但冗余代码太多
     * 3、尝试实现Condition接口或ConfigurationCondition接口，但如果在这些接口中使用DataSourceFactory，则会导致无法正确加载database.yml中的属性值
     *
     * @param dataSourceFactory
     * @return
     */
    @Bean
    public DataSourcePostProcessor initDataSourcePostProcessor(DataSourceFactory dataSourceFactory) {
        boolean enableXA = dataSourceFactory.isEnableXA();
        if (enableXA) return null;
        return new DataSourcePostProcessor();
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
     * @param dataSourceFactory
     * @return
     * @throws Exception
     */
    @Bean(name = Constant.FREEMARKER_CONFIGURATION)
    public freemarker.template.Configuration initConfiguration(DataSourceFactory dataSourceFactory) throws Exception {
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
        String macro = dataSourceFactory.getMacro();
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
     * @param dataSource
     */
    private void putDataSourceMap(Map<String, DataSource> dataSourceMap, DataSource dataSource) {
        if (dataSource == null) {
            return;
        }
        if (dataSource instanceof DruidDataSource) {
            dataSourceMap.put(((DruidDataSource) dataSource).getName(), dataSource);
        }
        if (dataSource instanceof AtomikosDataSourceBean) {
            dataSourceMap.put(((AtomikosDataSourceBean) dataSource).getUniqueResourceName(), dataSource);
        }
    }
}