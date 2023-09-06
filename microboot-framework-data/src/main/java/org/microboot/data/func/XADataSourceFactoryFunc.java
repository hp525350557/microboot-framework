package org.microboot.data.func;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author 胡鹏
 */
public interface XADataSourceFactoryFunc {
    DataSource rebuildDataSource(DataSource dataSource);

    String getDataSourceName(DataSource dataSource);

    void putDataSourceMap(Map<String, DataSource> dataSourceMap, DataSource dataSource);
}