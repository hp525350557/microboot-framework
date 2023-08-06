package org.microboot.data.basedao;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.constant.Constant;
import org.microboot.core.entity.Page;
import org.microboot.core.utils.ConvertUtils;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.List;
import java.util.Map;

/**
 * @author 胡鹏
 *
 * microboot框架在搭建的时候，就把主从读写分离的方案纳入进来了
 * 主从读写分离的问题：
 * Mysql主从同步是有延时的，因此当一个事务中，先执行execute方法将数据写到主库，然后query方法查询数据
 * 如果读取的是从库（读库），那么很可能会因为同步延时而导致读取不到
 *
 * 版本一解决方案：
 * 在BaseDao的继承关系中，加入了TransmittableThreadLocal作为父类
 * 让BaseDao本身也是一个ThreadLocal
 * 每次执行execute方法时，将写库的连接记录下来，并通过TransmittableThreadLocal进行传播
 * 每次执行query方法时，先查看TransmittableThreadLocal中有没有数据库连接
 * 1、如果有就表示先执行了增删改操作，那么查询时直接使用写库的连接去进行查询数据
 * 2、如果没有就表示读取数据之前没有对数据库进行增删改操作，那么就获取从库的连接进行读数据
 * 由于Spring默认使用了线程池，导致线程总是复用，为了避免ThreadLocal可能出现的内存泄露
 * 因此自定义了一个ClearThreadLocal注解和相应的Spring切面，将每次存在当前线程中的写库连接remove掉
 * 最后在Service类或方法上加上这个注解，即可
 *
 * 版本一的问题：
 * 在版本一的切面中，只是进行了简单的删除，但是后来又发现了问题
 * 当在Service类上加上ClearThreadLocal注解后，每个service方法都会被代理
 * 如果在service方法中有嵌套执行，比如：A() -> B()
 * 那么切面中的remove会执行两次
 *
 * 版本二解决方案：
 * 为了解决上面的问题，版本二准备在切面中记录最外层的方法，只有当最外层方法执行完时，才去调用TransmittableThreadLocal的remove方法
 *
 * 版本二的问题：
 * 虽然解决了嵌套调用的问题，但是如果遇到多线程，又会有问题了
 * 比如：A() -> thread(() -> b()).start()
 * 如果此时A()方法先执行完，那么A()可能不会去执行TransmittableThreadLocal的remove方法，导致不可遇见的问题
 *
 * 现版本解决方案：
 * 由于上面的方案有这么多的问题，于是决定不再使用TransmittableThreadLocal来解决主从延时问题，而是定义一个AbstractBaseDaoWithMaster类
 * 让AbstractBaseDaoWithMaster中的query方法默认使用主库连接，而BaseDao中的query方法默认使用从库连接
 * 交给上层业务开发人员去决定，什么时候查从库，什么时候查主库
 */
public abstract class AbstractBaseDaoWithMaster extends AbstractBaseDaoWithOthers {

    /**
     * @param templateName
     * @param parameters
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForListWithMaster(String templateName, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.MASTER_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.queryForList(templateName, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param javaBean
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForListWithMaster(String templateName, Object javaBean) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForListWithMaster(templateName, parameters);
    }

    /**
     * @param templateName
     * @param paramKey
     * @param paramValue
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForListWithMaster(String templateName, String paramKey, Object paramValue) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForListWithMaster(templateName, parameters);
    }

    /**
     * @param templateName
     * @param parameters
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> queryForListWithMaster(String templateName, Map<String, ?> parameters, Class<T> clazz) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.MASTER_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.queryForList(templateName, parameters, clazz, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param javaBean
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> queryForListWithMaster(String templateName, Object javaBean, Class<T> clazz) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForListWithMaster(templateName, parameters, clazz);
    }

    /**
     * @param templateName
     * @param paramKey
     * @param paramValue
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> queryForListWithMaster(String templateName, String paramKey, Object paramValue, Class<T> clazz) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForListWithMaster(templateName, parameters, clazz);
    }

    /**
     * @param templateName
     * @param parameters
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMapWithMaster(String templateName, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.MASTER_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.queryForMap(templateName, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param javaBean
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMapWithMaster(String templateName, Object javaBean) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForMapWithMaster(templateName, parameters);
    }

    /**
     * @param templateName
     * @param paramKey
     * @param paramValue
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMapWithMaster(String templateName, String paramKey, Object paramValue) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForMapWithMaster(templateName, parameters);
    }

    /**
     * @param templateName
     * @param parameters
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryForObjectWithMaster(String templateName, Map<String, ?> parameters, Class<T> clazz) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.MASTER_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.queryForObject(templateName, parameters, clazz, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param javaBean
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryForObjectWithMaster(String templateName, Object javaBean, Class<T> clazz) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForObjectWithMaster(templateName, parameters, clazz);
    }

    /**
     * @param templateName
     * @param paramKey
     * @param paramValue
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryForObjectWithMaster(String templateName, String paramKey, Object paramValue, Class<T> clazz) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForObjectWithMaster(templateName, parameters, clazz);
    }

    /**
     * @param templateName
     * @param parameters
     * @param rse
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryWithMaster(String templateName, Map<String, ?> parameters, ResultSetExtractor<T> rse) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.MASTER_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.query(templateName, parameters, namedParameterJdbcTemplate, rse);
    }

    /**
     * @param templateName
     * @param javaBean
     * @param rse
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryWithMaster(String templateName, Object javaBean, ResultSetExtractor<T> rse) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryWithMaster(templateName, parameters, rse);
    }

    /**
     * @param templateName
     * @param paramKey
     * @param paramValue
     * @param rse
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryWithMaster(String templateName, String paramKey, Object paramValue, ResultSetExtractor<T> rse) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryWithMaster(templateName, parameters, rse);
    }

    /**
     * @param templateName
     * @param parameters
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSetWithMaster(String templateName, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.MASTER_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.queryForSqlRowSet(templateName, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param javaBean
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSetWithMaster(String templateName, Object javaBean) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForSqlRowSetWithMaster(templateName, parameters);
    }

    /**
     * @param templateName
     * @param paramKey
     * @param paramValue
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSetWithMaster(String templateName, String paramKey, Object paramValue) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForSqlRowSetWithMaster(templateName, parameters);
    }

    /**
     * @param paginationCount
     * @param pagination
     * @param parameters
     * @return
     * @throws Exception
     */
    public Page paginationWithMaster(String paginationCount, String pagination, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.MASTER_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.pagination(paginationCount, pagination, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param paginationCount
     * @param pagination
     * @param javaBean
     * @return
     * @throws Exception
     */
    public Page paginationWithMaster(String paginationCount, String pagination, Object javaBean) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.paginationWithMaster(paginationCount, pagination, parameters);
    }

    /**
     * @param sql
     * @param parameters
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForListBySqlWithMaster(String sql, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.MASTER_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.queryForListBySql(sql, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param javaBean
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForListBySqlWithMaster(String sql, Object javaBean) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForListBySqlWithMaster(sql, parameters);
    }

    /**
     * @param sql
     * @param paramKey
     * @param paramValue
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForListBySqlWithMaster(String sql, String paramKey, Object paramValue) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForListBySqlWithMaster(sql, parameters);
    }

    /**
     * @param sql
     * @param parameters
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> queryForListBySqlWithMaster(String sql, Map<String, ?> parameters, Class<T> clazz) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.MASTER_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.queryForListBySql(sql, parameters, clazz, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param javaBean
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> queryForListBySqlWithMaster(String sql, Object javaBean, Class<T> clazz) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForListBySqlWithMaster(sql, parameters, clazz);
    }

    /**
     * @param sql
     * @param paramKey
     * @param paramValue
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> queryForListBySqlWithMaster(String sql, String paramKey, Object paramValue, Class<T> clazz) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForListBySqlWithMaster(sql, parameters, clazz);
    }

    /**
     * @param sql
     * @param parameters
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMapBySqlWithMaster(String sql, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.MASTER_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.queryForMapBySql(sql, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param javaBean
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMapBySqlWithMaster(String sql, Object javaBean) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForMapBySqlWithMaster(sql, parameters);
    }

    /**
     * @param sql
     * @param paramKey
     * @param paramValue
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMapBySqlWithMaster(String sql, String paramKey, Object paramValue) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForMapBySqlWithMaster(sql, parameters);
    }

    /**
     * @param sql
     * @param parameters
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryForObjectBySqlWithMaster(String sql, Map<String, ?> parameters, Class<T> clazz) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.MASTER_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.queryForObjectBySql(sql, parameters, clazz, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param javaBean
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryForObjectBySqlWithMaster(String sql, Object javaBean, Class<T> clazz) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForObjectBySqlWithMaster(sql, parameters, clazz);
    }

    /**
     * @param sql
     * @param paramKey
     * @param paramValue
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryForObjectBySqlWithMaster(String sql, String paramKey, Object paramValue, Class<T> clazz) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForObjectBySqlWithMaster(sql, parameters, clazz);
    }

    /**
     * @param sql
     * @param parameters
     * @param rse
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryBySqlWithMaster(String sql, Map<String, ?> parameters, ResultSetExtractor<T> rse) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.MASTER_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.queryBySql(sql, parameters, namedParameterJdbcTemplate, rse);
    }

    /**
     * @param sql
     * @param javaBean
     * @param rse
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryBySqlWithMaster(String sql, Object javaBean, ResultSetExtractor<T> rse) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryBySqlWithMaster(sql, parameters, rse);
    }

    /**
     * @param sql
     * @param paramKey
     * @param paramValue
     * @param rse
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryBySqlWithMaster(String sql, String paramKey, Object paramValue, ResultSetExtractor<T> rse) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryBySqlWithMaster(sql, parameters, rse);
    }

    /**
     * @param sql
     * @param parameters
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSetBySqlWithMaster(String sql, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.MASTER_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.queryForSqlRowSetBySql(sql, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param javaBean
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSetBySqlWithMaster(String sql, Object javaBean) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForSqlRowSetBySqlWithMaster(sql, parameters);
    }

    /**
     * @param sql
     * @param paramKey
     * @param paramValue
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSetBySqlWithMaster(String sql, String paramKey, Object paramValue) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForSqlRowSetBySqlWithMaster(sql, parameters);
    }

    /**
     * @param paginationCountSql
     * @param paginationSql
     * @param parameters
     * @return
     * @throws Exception
     */
    public Page paginationBySqlWithMaster(String paginationCountSql, String paginationSql, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.MASTER_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.paginationBySql(paginationCountSql, paginationSql, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param paginationCountSql
     * @param paginationSql
     * @param javaBean
     * @return
     * @throws Exception
     */
    public Page paginationBySqlWithMaster(String paginationCountSql, String paginationSql, Object javaBean) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.paginationBySqlWithMaster(paginationCountSql, paginationSql, parameters);
    }
}