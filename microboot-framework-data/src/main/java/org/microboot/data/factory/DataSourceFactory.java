package org.microboot.data.factory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author 胡鹏
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "datasource")
public class DataSourceFactory {

    //写数据库连接池信息
    private Map<String, Object> master;

    //读数据库连接池信息
    private List<Map<String, Object>> slaves;

    //其他数据库连接池信息
    private List<Map<String, Object>> others;

    //DruidDataSource配置项前缀
    private final String prefix = "druid.";

    //分布式事务开关配置，默认关闭
    private boolean enableXA;

    /**
     * 创建DruidDataSource或DruidXADataSource
     *
     * @param parameters
     * @return
     */
    public DruidDataSource createDataSource(Map<String, Object> parameters) {
        //连接池配置
        Map<String, Object> dataSourceMap = Maps.newHashMap();
        //master是默认配置
        dataSourceMap.putAll(master);
        //parameters是差异化配置，可覆盖或补全master中相同的key的value
        dataSourceMap.putAll(parameters);

        //创建连接池对象
        DruidDataSource druidDataSource;
        if (enableXA) {
            druidDataSource = new DruidXADataSource();
        } else {
            druidDataSource = new DruidDataSource();
        }

        //封装连接池配置
        Properties properties = new Properties();
        for (String key : dataSourceMap.keySet()) {
            String value = MapUtils.getString(dataSourceMap, key);
            if (StringUtils.isBlank(value)) {
                continue;
            }
            properties.setProperty(prefix + key, value);
        }

        //初始化连接池配置
        druidDataSource.configFromPropety(properties);

        return druidDataSource;
    }

    /**
     * 创建AtomikosDataSourceBean
     *
     * @param dataSource
     * @return
     */
    public AtomikosDataSourceBean createAtomikosDataSourceBean(DataSource dataSource) {
        if (dataSource instanceof DruidXADataSource) {
            AtomikosDataSourceBean atomikosDataSource = new AtomikosDataSourceBean();
            atomikosDataSource.setUniqueResourceName(((DruidXADataSource) dataSource).getName());
            atomikosDataSource.setXaDataSource((DruidXADataSource) dataSource);
            return atomikosDataSource;
        }
        return null;
    }
}
