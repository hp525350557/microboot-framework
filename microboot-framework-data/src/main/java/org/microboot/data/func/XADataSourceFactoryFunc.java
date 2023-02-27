package org.microboot.data.func;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author 胡鹏
 */
public interface XADataSourceFactoryFunc {
    DataSource rebuildDataSource(DataSource dataSource);

    DruidDataSource getDruidDataSource(DataSource dataSource);

    void putDataSourceMap(Map<String, DataSource> dataSourceMap, DataSource dataSource);
}