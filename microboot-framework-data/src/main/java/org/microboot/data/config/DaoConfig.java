package org.microboot.data.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Maps;
import freemarker.ext.beans.BeansWrapperBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.constant.Constant;
import org.microboot.data.basedao.BaseDao;
import org.microboot.data.factory.DataSourceFactory;
import org.microboot.data.func.XADataSourceFactoryFunc;
import org.microboot.data.processor.DataSourcePostProcessor;
import org.microboot.data.resolver.TemplateResolver;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author 胡鹏
 * 在老版本中，microboot-framework-data内置了从库集群的负载均衡，从库节点退避以及回归等功能
 * 但作者在不断学习过程中，接触到了LVS+keepalived的方案
 * · LVS可以实现IP层面的负载均衡
 * · keepalived可以实现LVS的高可用，还能对LVS负载的real server节点进行健康检查，并让宕机的节点退避
 * 这些功能完全覆盖了microboot-framework-data内置的从库逻辑
 * 因此新版版中，将从库的负载均衡，退避等相关功能全部去掉了
 * 这部分功能在以后的架构中交由LVS+keepalived或其他类似功能去实现
 */
@Configuration
@DependsOn(Constant.APPLICATION_CONTEXT_HOLDER)
public class DaoConfig {

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
        DataSource dataSource = dataSourceFactory.createDataSource(master);
        if (dataSourceFactory.isEnableXA()) {
            Asserts.check(
                    ApplicationContextHolder.getApplicationContext().containsLocalBean(XADataSourceFactoryFunc.class.getName()),
                    XADataSourceFactoryFunc.class.getName().concat(" is missing")
            );
            dataSource = ApplicationContextHolder.getBean(XADataSourceFactoryFunc.class.getName(), XADataSourceFactoryFunc.class)
                    .rebuildDataSource(dataSource);
            Asserts.check(dataSource != null, "dataSource is null");
        }
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
     * 初始化DataSource（从库）
     *
     * @param dataSourceFactory
     * @return
     */
    @Bean(value = Constant.SLAVES_DATA_SOURCE)
    public DataSource initSlavesDataSource(DataSourceFactory dataSourceFactory) {
        Map<String, DataSource> dataSourceMap = Maps.newHashMap();
        Map<String, Object> slaves = dataSourceFactory.getSlaves();
        DataSource dataSource;
        if (MapUtils.isEmpty(slaves)) {
            //如果未定义从库，则主从用同一个数据源
            dataSource = ApplicationContextHolder.getBean(Constant.MASTER_DATA_SOURCE, DataSource.class);
        } else {
            //如果定义了从库，则主从分离，主数据库用来写，从数据库用来读
            dataSource = dataSourceFactory.createDataSource(slaves);
            //如果开启了XA模式，则对dataSource进行XA处理
            if (dataSourceFactory.isEnableXA()) {
                Asserts.check(
                        ApplicationContextHolder.getApplicationContext().containsLocalBean(XADataSourceFactoryFunc.class.getName()),
                        XADataSourceFactoryFunc.class.getName().concat(" is missing")
                );
                dataSource = ApplicationContextHolder.getBean(XADataSourceFactoryFunc.class.getName(), XADataSourceFactoryFunc.class)
                        .rebuildDataSource(dataSource);
                Asserts.check(dataSource != null, "dataSource is null");
            }
        }
        return dataSource;
    }

    /**
     * 初始化NamedParameterJdbcTemplate（从库）
     *
     * @return
     */
    @Bean(name = Constant.SLAVES_JDBC_TEMPLATE)
    public NamedParameterJdbcTemplate initSlavesJdbcTemplate() {
        DataSource dataSource = ApplicationContextHolder.getBean(Constant.SLAVES_DATA_SOURCE, DataSource.class);
        return new NamedParameterJdbcTemplate(dataSource);
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
                DataSource dataSource = dataSourceFactory.createDataSource(other);
                //如果开启了XA模式，则对dataSource进行XA处理
                if (dataSourceFactory.isEnableXA()) {
                    Asserts.check(
                            ApplicationContextHolder.getApplicationContext().containsLocalBean(XADataSourceFactoryFunc.class.getName()),
                            XADataSourceFactoryFunc.class.getName().concat(" is missing")
                    );
                    dataSource = ApplicationContextHolder.getBean(XADataSourceFactoryFunc.class.getName(), XADataSourceFactoryFunc.class)
                            .rebuildDataSource(dataSource);
                    Asserts.check(dataSource != null, "dataSource is null");
                    ApplicationContextHolder.getBean(XADataSourceFactoryFunc.class.getName(), XADataSourceFactoryFunc.class)
                            .putDataSourceMap(dataSourceMap, dataSource);
                } else {
                    dataSourceMap.put(((DruidDataSource) dataSource).getName(), dataSource);
                }
            }
        }
        return dataSourceMap;
    }

    /**
     * 初始化Map<String, NamedParameterJdbcTemplate>（其他库）
     *
     * @return
     */
    @Bean(name = Constant.OTHERS_JDBC_TEMPLATE)
    public Map<String, NamedParameterJdbcTemplate> initOthersJdbcTemplate() {
        Map<String, DataSource> othersDataSourceMap = (Map<String, DataSource>) ApplicationContextHolder.getBean(Constant.OTHERS_DATA_SOURCE);
        Map<String, NamedParameterJdbcTemplate> othersJdbcTemplateMap = Maps.newConcurrentMap();
        for (String name : othersDataSourceMap.keySet()) {
            othersJdbcTemplateMap.put(name, new NamedParameterJdbcTemplate(othersDataSourceMap.get(name)));
        }
        return othersJdbcTemplateMap;
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
     * 初始化freemarker.template.Configuration
     *
     * @param dataSourceFactory
     * @return
     * @throws Exception
     */
    @Bean(name = Constant.FREEMARKER_CONFIGURATION)
    public freemarker.template.Configuration initConfiguration(DataSourceFactory dataSourceFactory, FreeMarkerProperties properties) throws Exception {
        freemarker.template.Configuration configuration = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_24);

        //本来想完全交给SpringBoot去管理，直接在配置文件中写配置信息即可
        //但是测试发现beansWrapperFn会报错，可能是因为不是在构建Bean的时候设置的
        //所以参考SpringBoot的构建方式，手动创建，现在settings属性还是在配置文件中设置
        configuration.setDefaultEncoding(properties.getCharsetName());
        Properties settings = new Properties();
        settings.putAll(properties.getSettings());
        configuration.setSettings(settings);
        //用于整合Java工具类
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
             4、autoImport.put("key", "value")
                    key：表示指向value这个模板
                    value：表示模板名【默认是用模板的相对路径做templateKey】，TemplateResolver将模板名本身作为templateKey
                如果加载的macro.sql改名叫XXX.sql，那么basedao-*.yml文件中的datasource.macro就应该配置为XXX
                那么这里的autoImport的key还是macro，value就是XXX
		 */
        String macro = dataSourceFactory.getMacro();
        if (StringUtils.isNotBlank(macro)) {
            Map<String, Object> autoImport = Maps.newHashMap();
            autoImport.put("macro", macro);
            configuration.setAutoImports(autoImport);
        }
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
}