package org.microboot.data.runner;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.alibaba.druid.util.JdbcUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.constant.Constant;
import org.microboot.core.utils.LoggerUtils;
import org.microboot.data.container.DataContainer;
import org.microboot.data.factory.DataSourceFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 胡鹏
 */
public class StartRunner implements ApplicationRunner {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final DataSourceFactory dataSourceFactory = ApplicationContextHolder.getBean(DataSourceFactory.class);

    private final long backoffTimeDelay = dataSourceFactory.getBackoffTimeDelay();

    private final long backoffTimePeriod = dataSourceFactory.getBackoffTimePeriod();

    private final long backoffTimeLimit = dataSourceFactory.getBackoffTimeLimit();

    private final long backoffTimeLimitMax = dataSourceFactory.getBackoffTimeLimitMax();

    private final int backoffTimeLimitStep = dataSourceFactory.getBackoffTimeLimitStep();

    @SuppressWarnings("unchecked")
    @Override
    public void run(ApplicationArguments args) throws SQLException {
        Map<String, DataSource> slavesDataSourceMap = (Map<String, DataSource>) ApplicationContextHolder.getBean(Constant.SLAVES_DATA_SOURCE);
        Map<String, DataSource> othersDataSourceMap = (Map<String, DataSource>) ApplicationContextHolder.getBean(Constant.OTHERS_DATA_SOURCE);
        for (String name : slavesDataSourceMap.keySet()) {
            DataContainer.slavesMap.put(name, new NamedParameterJdbcTemplate(slavesDataSourceMap.get(name)));
        }
        for (String name : othersDataSourceMap.keySet()) {
            DataContainer.othersMap.put(name, new NamedParameterJdbcTemplate(othersDataSourceMap.get(name)));
        }
        //如果主从连接同一个库，则不需要开启退避
        DataSource dataSource = ApplicationContextHolder.getBean(Constant.MASTER_DATA_SOURCE, DataSource.class);
        DruidDataSource druidDataSource = this.getDruidDataSource(dataSource);
        String dataSourceName = druidDataSource.getName();
        if (slavesDataSourceMap.size() == 1 && slavesDataSourceMap.containsKey(dataSourceName)) {
            return;
        }
        DataContainer.initMap.putAll(DataContainer.slavesMap);
        //启动定时器
        ScheduledThreadPoolExecutor service = new ScheduledThreadPoolExecutor(1);
        service.setMaximumPoolSize(1);
        service.scheduleWithFixedDelay(() -> {
            try {
                /*
                    测试连接，并将异常连接加入退避容器
                    这里将Map的keySet转换成了List后再做parallelStream()操作是因为经过实验发现：
                        Set的parallelStream()并没有根据CPU核数来进行并发处理
                    这里选用多线程并发（并发限制交给了JUC，与CPU核数有关）的方式测试退避，
                        因为串行化会受到连接测试时的超时时间影响，
                        导致排在后面的连接可能已经断开了，但却需要等到前面的连接都测试完才能被测试到
                 */
                Lists.newArrayList(DataContainer.initMap.keySet().iterator()).parallelStream().forEach(name -> {
                    //从退避集合获取对应连接名的退避时间点
                    //1、如果为0则表示name对应的连接未加入退避
                    //2、如果非0则表示name对应的连接已加入退避
                    long backoffTime = MapUtils.getLongValue(DataContainer.backoffTimeMap, name, 0L);
                    //当前系统时间
                    long currentTime = System.currentTimeMillis();
                    //上次退避上限时间
                    long oldBackoffTimeLimit = MapUtils.getLongValue(DataContainer.backoffTimeLimitMap, name, 0L);
                    //本次退避上限时间
                    long newBackoffTimeLimit = NumberUtils.min(backoffTimeLimitMax, NumberUtils.max(oldBackoffTimeLimit, backoffTimeLimit));
                    //判断当前连接是否处于退避期间：
                    //1、退避时间点非0
                    //2、(退避时间点 + 退避上限时间)大于当前时间
                    if (backoffTime != 0 && (backoffTime + newBackoffTimeLimit) > currentTime) {
                        //尚未达到恢复期
                        return;
                    }
                    NamedParameterJdbcTemplate namedParameterJdbcTemplate = DataContainer.initMap.get(name);
                    try {
                        this.validateConnection(namedParameterJdbcTemplate);
                        //移除退避
                        setBackoffTime(name, namedParameterJdbcTemplate, false, 0L);
                    } catch (Exception e) {
                        LoggerUtils.error(logger, e);
                        //添加退避
                        setBackoffTime(name, namedParameterJdbcTemplate, true, newBackoffTimeLimit * backoffTimeLimitStep);
                    }
                });
            } catch (Exception e) {
                LoggerUtils.error(logger, e);
            }
        }, backoffTimeDelay, backoffTimePeriod, TimeUnit.MILLISECONDS);
    }

    /**
     * 设置退避
     *
     * @param name
     * @param namedParameterJdbcTemplate
     * @param isBackoff
     * @param backoffTimeLimit
     */
    private void setBackoffTime(String name, NamedParameterJdbcTemplate namedParameterJdbcTemplate, boolean isBackoff, long backoffTimeLimit) {
        if (isBackoff) {
            //添加退避
            DataContainer.slavesMap.remove(name);
            DataContainer.backoffTimeMap.put(name, System.currentTimeMillis());
        } else {
            //移除退避
            DataContainer.slavesMap.put(name, namedParameterJdbcTemplate);
            DataContainer.backoffTimeMap.remove(name);
        }
        DataContainer.backoffTimeLimitMap.put(name, backoffTimeLimit);
    }

    /**
     * 验证连接
     *
     * @param namedParameterJdbcTemplate
     * @throws Exception
     */
    private void validateConnection(NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
        DataSource dataSource = namedParameterJdbcTemplate.getJdbcTemplate().getDataSource();
        DruidDataSource druidDataSource = this.getDruidDataSource(dataSource);
        String url = druidDataSource.getUrl();
        String username = druidDataSource.getUsername();
        String password = druidDataSource.getPassword();
        String driverClassName = druidDataSource.getDriverClassName();
        Connection connection = null;
        try {
            Class.forName(driverClassName);
            connection = DriverManager.getConnection(url, username, password);
        } finally {
            JdbcUtils.close(connection);
        }
    }

    /**
     * 获取DruidDataSource
     *
     * 老版本中，可以强转成DruidDataSource
     * 但新版中加入了分布式事务数据源AtomikosDataSourceBean
     * AtomikosDataSourceBean不是DruidDataSource的子类
     * 因此不能直接强转，需要判断后进行转换
     *
     * @param dataSource
     * @return
     */
    private DruidDataSource getDruidDataSource(DataSource dataSource) {
        DruidDataSource druidDataSource;
        if (dataSource instanceof DruidDataSource) {
            druidDataSource = (DruidDataSource) dataSource;
        } else {
            AtomikosDataSourceBean atomikosDataSourceBean = (AtomikosDataSourceBean) dataSource;
            druidDataSource = (DruidXADataSource) atomikosDataSourceBean.getXaDataSource();
        }
        return druidDataSource;
    }
}
