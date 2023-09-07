package org.microboot.data.func;

import javax.sql.DataSource;

/**
 * @author 胡鹏
 */
public interface XADataSourceFactoryFunc {
    DataSource rebuildDataSource(DataSource dataSource);

    String getDataSourceName(DataSource dataSource);
}