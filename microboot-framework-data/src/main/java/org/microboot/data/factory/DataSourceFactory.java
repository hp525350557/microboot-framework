package org.microboot.data.factory;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

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

    /**
     * 创建
     *
     * @param dataSourceMap
     * @return
     */
    public DruidDataSource createDataSource(Map<String, Object> dataSourceMap) {
        DruidDataSource druidDataSource = new DruidDataSource();
        Properties properties = new Properties();
        String prefix = "druid.";
        master.putAll(dataSourceMap);
        for (String key : master.keySet()) {
            String value = MapUtils.getString(master, key);
            if (StringUtils.isBlank(value)) {
                continue;
            }
            properties.setProperty(prefix + key, value);
        }
        druidDataSource.configFromPropety(properties);
        return druidDataSource;
    }
}
