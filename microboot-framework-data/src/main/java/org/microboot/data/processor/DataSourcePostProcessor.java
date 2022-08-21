package org.microboot.data.processor;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.collections4.MapUtils;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.constant.Constant;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Set;

/**
 * @author 胡鹏
 *
 * other库用@Bean的方式构建可以实现XA分布式事务，用DefaultListableBeanFactory手动注入方式不行的原因：
 * NamedParameterJdbcTemplate在创建的时候传入的是DruidXADataSource对象，而不是AtomikosDataSourceBean
 * NamedParameterJdbcTemplate在执行sql时，底层要获取分布式事务的连接池
 * XA必须用AtomikosDataSourceBean才可以
 * 主库之所有可以成功，并不是因为@Bean，而是因为在返回DataSource对象时就已经封装成AtomikosDataSourceBean了
 * 所以构建主库的NamedParameterJdbcTemplate时，用的就是AtomikosDataSourceBean对象
 * 从库之所以不能成功，是因为返回的Map中装的还是DruidXADataSource
 *
 * 下面是这次为了解决这个bug时学习到的一些知识：
 * 1、SpringBoot启动后，会先将所有注入的Bean的名称存到一个集合中，然后再轮询去构建bean实例
 * refreshContext -> refresh ... -> finishBeanFactoryInitialization(beanFactory)
 * -> beanFactory.preInstantiateSingletons() -> beanDefinitionNames集合
 *
 * 2、beanDefinitionNames集合是有顺序的，按照SpringBoot集中扫描和注入规则依次添加进去的
 * 但是如果希望将BeanDefinitionName集合中的位置靠前存放，那么可以通过实现Listener的方式
 * 根据不同的event可以在不同的位置执行我们注入的代码，具体实现可以参考：ConfigFileApplicationListener
 *
 * 3、BeanFactoryPostProcessor和BeanDefinitionRegistryPostProcessor都可以在Bean实例创建前做一些操作
 * 比如：添加自定义的BeanDefinition，
 * 注意：在BeanFactoryPostProcessor和BeanDefinitionRegistryPostProcessor无法获取我们定义的Bean实例
 * 因为此时还没有实例化
 */
public class DataSourcePostProcessor implements InitializingBean {

    //将applicationContext转换为ConfigurableApplicationContext
    private final ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) ApplicationContextHolder.getApplicationContext();
    //获取bean工厂并转换为DefaultListableBeanFactory
    private final DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();

    @Override
    public void afterPropertiesSet() throws Exception {
        DataSource dataSource = ApplicationContextHolder.getBean(Constant.MASTER_DATA_SOURCE, DataSource.class);
        //默认事务管理器
        defaultTransactionManager(dataSource);

        Map<String, DataSource> othersDataSourceMap = (Map<String, DataSource>) ApplicationContextHolder.getBean(Constant.OTHERS_DATA_SOURCE);
        //动态事务管理器
        this.dynamicTransactionManager(othersDataSourceMap);
    }

    /**
     * 默认事务管理器
     *
     * @param dataSource
     */
    private void defaultTransactionManager(DataSource dataSource) {
        //通过BeanDefinitionBuilder创建bean定义
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceTransactionManager.class);
        //设置为默认事务管理器
        beanDefinitionBuilder.setPrimary(true);
        //设置bean属性
        beanDefinitionBuilder.addPropertyValue("dataSource", dataSource);
        //设置bean属性
        beanDefinitionBuilder.addPropertyValue("nestedTransactionAllowed", true);
        //注册bean
        defaultListableBeanFactory.registerBeanDefinition("transactionManager", beanDefinitionBuilder.getRawBeanDefinition());
    }

    /**
     * 动态事务管理器
     *
     * @param dataSourceMap
     * @return
     */
    private void dynamicTransactionManager(Map<String, DataSource> dataSourceMap) {
        if (MapUtils.isEmpty(dataSourceMap)) {
            return;
        }

        Set<String> dataSourceNames = dataSourceMap.keySet();

        for (String dataSourceName : dataSourceNames) {
            DataSource dataSource = dataSourceMap.get(dataSourceName);
            if (dataSource == null) {
                continue;
            }
            if (dataSource instanceof DruidDataSource) {
                //通过BeanDefinitionBuilder创建bean定义
                BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceTransactionManager.class);
                //设置bean属性
                beanDefinitionBuilder.addPropertyValue("dataSource", dataSource);
                //注册bean
                defaultListableBeanFactory.registerBeanDefinition(DataSourceTransactionManager.class.getName() + '$' + dataSourceName, beanDefinitionBuilder.getRawBeanDefinition());
            }
        }
    }
}
