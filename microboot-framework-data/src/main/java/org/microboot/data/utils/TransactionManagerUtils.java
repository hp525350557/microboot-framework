package org.microboot.data.utils;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.collections.MapUtils;
import org.microboot.core.bean.ApplicationContextHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Set;

/**
 * @author 胡鹏
 */
public class TransactionManagerUtils {

    private static final String transactionManagerPrefix = "transactionManager";

    /**
     * 默认事务管理器
     *
     * @param dataSource
     */
    public static void defaultTransactionManager(DataSource dataSource) {
        //将applicationContext转换为ConfigurableApplicationContext
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) ApplicationContextHolder.getApplicationContext();
        //获取bean工厂并转换为DefaultListableBeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();

        //通过BeanDefinitionBuilder创建bean定义
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceTransactionManager.class);
        //设置为默认事务管理器
        beanDefinitionBuilder.setPrimary(true);
        //设置bean属性
        beanDefinitionBuilder.addPropertyValue("dataSource", dataSource);
        //设置bean属性
        beanDefinitionBuilder.addPropertyValue("nestedTransactionAllowed", true);
        //注册bean
        defaultListableBeanFactory.registerBeanDefinition(transactionManagerPrefix, beanDefinitionBuilder.getRawBeanDefinition());
    }

    /**
     * 动态事务管理器
     *
     * @param dataSource
     */
    public static void dynamicTransactionManager(DataSource dataSource) {
        if (dataSource == null) {
            return;
        }

        //将applicationContext转换为ConfigurableApplicationContext
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) ApplicationContextHolder.getApplicationContext();
        //获取bean工厂并转换为DefaultListableBeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();

        if (dataSource instanceof DruidDataSource) {
            //通过BeanDefinitionBuilder创建bean定义
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceTransactionManager.class);
            //设置bean属性
            beanDefinitionBuilder.addPropertyValue("dataSource", dataSource);
            //注册bean
            defaultListableBeanFactory.registerBeanDefinition(transactionManagerPrefix + '&' + ((DruidDataSource) dataSource).getName(), beanDefinitionBuilder.getRawBeanDefinition());
        }
    }

    /**
     * 动态事务管理器
     *
     * @param jdbcTemplateMap
     */
    public static void dynamicTransactionManager(Map<String, NamedParameterJdbcTemplate> jdbcTemplateMap) {
        if (MapUtils.isEmpty(jdbcTemplateMap)) {
            return;
        }

        //将applicationContext转换为ConfigurableApplicationContext
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) ApplicationContextHolder.getApplicationContext();
        //获取bean工厂并转换为DefaultListableBeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();

        final Set<String> jdbcTemplateKeys = jdbcTemplateMap.keySet();

        for (String dataSourceName : jdbcTemplateKeys) {
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = jdbcTemplateMap.get(dataSourceName);
            if (namedParameterJdbcTemplate == null) {
                continue;
            }
            DataSource dataSource = namedParameterJdbcTemplate.getJdbcTemplate().getDataSource();
            if (dataSource instanceof DruidDataSource) {
                //通过BeanDefinitionBuilder创建bean定义
                BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceTransactionManager.class);
                //设置bean属性
                beanDefinitionBuilder.addPropertyValue("dataSource", dataSource);
                //注册bean
                defaultListableBeanFactory.registerBeanDefinition(transactionManagerPrefix + '&' + dataSourceName, beanDefinitionBuilder.getRawBeanDefinition());
            }
        }
    }
}
