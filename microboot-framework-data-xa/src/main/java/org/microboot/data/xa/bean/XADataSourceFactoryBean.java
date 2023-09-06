package org.microboot.data.xa.bean;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.xa.DruidXADataSource;
import org.microboot.data.func.XADataSourceFactoryFunc;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author 胡鹏
 */
public class XADataSourceFactoryBean implements XADataSourceFactoryFunc {

    /**
     * 创建AtomikosDataSourceBean
     *
     * @param dataSource
     * @return
     */
    @Override
    public DataSource rebuildDataSource(DataSource dataSource) {
        if (dataSource instanceof DruidXADataSource) {
            AtomikosDataSourceBean atomikosDataSource = new AtomikosDataSourceBean();
            atomikosDataSource.setUniqueResourceName(((DruidXADataSource) dataSource).getName());
            atomikosDataSource.setXaDataSource((DruidXADataSource) dataSource);
            return atomikosDataSource;
        }
        return dataSource;
    }

    /**
     * 获取分布式数据源的DruidDataSource
     *
     * @param dataSource
     * @return
     */
    @Override
    public DruidDataSource getDruidDataSource(DataSource dataSource) {
        AtomikosDataSourceBean atomikosDataSourceBean = (AtomikosDataSourceBean) dataSource;
        return (DruidXADataSource) atomikosDataSourceBean.getXaDataSource();
    }

    /**
     * 构建name->DruidDataSource的关系
     *
     * @param dataSourceMap
     * @param dataSource
     */
    @Override
    public void putDataSourceMap(Map<String, DataSource> dataSourceMap, DataSource dataSource) {
        if (dataSourceMap == null || dataSource == null) {
            return;
        }
        if (dataSource instanceof AtomikosDataSourceBean) {
            dataSourceMap.put(((AtomikosDataSourceBean) dataSource).getUniqueResourceName(), dataSource);
        }
    }
}