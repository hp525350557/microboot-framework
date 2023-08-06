package org.microboot.data.basedao;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.constant.Constant;
import org.microboot.core.entity.Page;
import org.microboot.core.utils.ConvertUtils;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.List;
import java.util.Map;

/**
 * @author 胡鹏
 */
public class BaseDao extends AbstractBaseDaoWithMaster {

    private final Logger logger = LogManager.getLogger(this.getClass());

    /**
     * @param templateName
     * @param parameters
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForList(String templateName, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.SLAVES_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.queryForList(templateName, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param javaBean
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForList(String templateName, Object javaBean) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForList(templateName, parameters);
    }

    /**
     * @param templateName
     * @param paramKey
     * @param paramValue
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForList(String templateName, String paramKey, Object paramValue) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForList(templateName, parameters);
    }

    /**
     * @param templateName
     * @param parameters
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> queryForList(String templateName, Map<String, ?> parameters, Class<T> clazz) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.SLAVES_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
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
    public <T> List<T> queryForList(String templateName, Object javaBean, Class<T> clazz) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForList(templateName, parameters, clazz);
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
    public <T> List<T> queryForList(String templateName, String paramKey, Object paramValue, Class<T> clazz) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForList(templateName, parameters, clazz);
    }

    /**
     * @param templateName
     * @param parameters
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMap(String templateName, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.SLAVES_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.queryForMap(templateName, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param javaBean
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMap(String templateName, Object javaBean) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForMap(templateName, parameters);
    }

    /**
     * @param templateName
     * @param paramKey
     * @param paramValue
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMap(String templateName, String paramKey, Object paramValue) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForMap(templateName, parameters);
    }

    /**
     * @param templateName
     * @param parameters
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryForObject(String templateName, Map<String, ?> parameters, Class<T> clazz) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.SLAVES_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
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
    public <T> T queryForObject(String templateName, Object javaBean, Class<T> clazz) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForObject(templateName, parameters, clazz);
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
    public <T> T queryForObject(String templateName, String paramKey, Object paramValue, Class<T> clazz) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForObject(templateName, parameters, clazz);
    }

    /**
     * @param templateName
     * @param parameters
     * @param rse
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T query(String templateName, Map<String, ?> parameters, ResultSetExtractor<T> rse) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.SLAVES_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
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
    public <T> T query(String templateName, Object javaBean, ResultSetExtractor<T> rse) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.query(templateName, parameters, rse);
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
    public <T> T query(String templateName, String paramKey, Object paramValue, ResultSetExtractor<T> rse) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.query(templateName, parameters, rse);
    }

    /**
     * @param templateName
     * @param parameters
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSet(String templateName, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.SLAVES_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.queryForSqlRowSet(templateName, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param javaBean
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSet(String templateName, Object javaBean) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForSqlRowSet(templateName, parameters);
    }

    /**
     * @param templateName
     * @param paramKey
     * @param paramValue
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSet(String templateName, String paramKey, Object paramValue) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForSqlRowSet(templateName, parameters);
    }

    /**
     * @param paginationCount
     * @param pagination
     * @param parameters
     * @return
     * @throws Exception
     */
    public Page pagination(String paginationCount, String pagination, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.SLAVES_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.pagination(paginationCount, pagination, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param paginationCount
     * @param pagination
     * @param javaBean
     * @return
     * @throws Exception
     */
    public Page pagination(String paginationCount, String pagination, Object javaBean) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.pagination(paginationCount, pagination, parameters);
    }

    /**
     * @param sql
     * @param parameters
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForListBySql(String sql, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.SLAVES_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.queryForListBySql(sql, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param javaBean
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForListBySql(String sql, Object javaBean) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForListBySql(sql, parameters);
    }

    /**
     * @param sql
     * @param paramKey
     * @param paramValue
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForListBySql(String sql, String paramKey, Object paramValue) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForListBySql(sql, parameters);
    }

    /**
     * @param sql
     * @param parameters
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> queryForListBySql(String sql, Map<String, ?> parameters, Class<T> clazz) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.SLAVES_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
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
    public <T> List<T> queryForListBySql(String sql, Object javaBean, Class<T> clazz) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForListBySql(sql, parameters, clazz);
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
    public <T> List<T> queryForListBySql(String sql, String paramKey, Object paramValue, Class<T> clazz) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForListBySql(sql, parameters, clazz);
    }

    /**
     * @param sql
     * @param parameters
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMapBySql(String sql, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.SLAVES_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.queryForMapBySql(sql, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param javaBean
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMapBySql(String sql, Object javaBean) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForMapBySql(sql, parameters);
    }

    /**
     * @param sql
     * @param paramKey
     * @param paramValue
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMapBySql(String sql, String paramKey, Object paramValue) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForMapBySql(sql, parameters);
    }

    /**
     * @param sql
     * @param parameters
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryForObjectBySql(String sql, Map<String, ?> parameters, Class<T> clazz) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.SLAVES_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
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
    public <T> T queryForObjectBySql(String sql, Object javaBean, Class<T> clazz) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForObjectBySql(sql, parameters, clazz);
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
    public <T> T queryForObjectBySql(String sql, String paramKey, Object paramValue, Class<T> clazz) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForObjectBySql(sql, parameters, clazz);
    }

    /**
     * @param sql
     * @param parameters
     * @param rse
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryBySql(String sql, Map<String, ?> parameters, ResultSetExtractor<T> rse) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.SLAVES_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
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
    public <T> T queryBySql(String sql, Object javaBean, ResultSetExtractor<T> rse) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryBySql(sql, parameters, rse);
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
    public <T> T queryBySql(String sql, String paramKey, Object paramValue, ResultSetExtractor<T> rse) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryBySql(sql, parameters, rse);
    }

    /**
     * @param sql
     * @param parameters
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSetBySql(String sql, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.SLAVES_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.queryForSqlRowSetBySql(sql, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param javaBean
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSetBySql(String sql, Object javaBean) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForSqlRowSetBySql(sql, parameters);
    }

    /**
     * @param sql
     * @param paramKey
     * @param paramValue
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSetBySql(String sql, String paramKey, Object paramValue) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForSqlRowSetBySql(sql, parameters);
    }

    /**
     * @param paginationCountSql
     * @param paginationSql
     * @param parameters
     * @return
     * @throws Exception
     */
    public Page paginationBySql(String paginationCountSql, String paginationSql, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.SLAVES_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.paginationBySql(paginationCountSql, paginationSql, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param paginationCountSql
     * @param paginationSql
     * @param javaBean
     * @return
     * @throws Exception
     */
    public Page paginationBySql(String paginationCountSql, String paginationSql, Object javaBean) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.paginationBySql(paginationCountSql, paginationSql, parameters);
    }

    /**
     * @param templateName
     * @param parameters
     * @return
     * @throws Exception
     */
    public int execute(String templateName, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.MASTER_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.execute(templateName, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param javaBean
     * @return
     * @throws Exception
     */
    public int execute(String templateName, Object javaBean) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.execute(templateName, parameters);
    }

    /**
     * @param templateName
     * @param paramKey
     * @param paramValue
     * @return
     * @throws Exception
     */
    public int execute(String templateName, String paramKey, Object paramValue) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.execute(templateName, parameters);
    }

    /**
     * @param templateName
     * @param parameterSource
     * @return
     * @throws Exception
     */
    public int execute(String templateName, MapSqlParameterSource parameterSource) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.MASTER_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.execute(templateName, parameterSource, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param parametersList
     * @return
     * @throws Exception
     */
    public int[] executeBatch(String templateName, List<?> parametersList) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.MASTER_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.executeBatch(templateName, parametersList, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param parameters
     * @return
     * @throws Exception
     */
    public int executeBySql(String sql, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.MASTER_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.executeBySql(sql, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param javaBean
     * @return
     * @throws Exception
     */
    public int executeBySql(String sql, Object javaBean) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.executeBySql(sql, parameters);
    }

    /**
     * @param sql
     * @param paramKey
     * @param paramValue
     * @return
     * @throws Exception
     */
    public int executeBySql(String sql, String paramKey, Object paramValue) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.executeBySql(sql, parameters);
    }

    /**
     * @param sql
     * @param parameterSource
     * @return
     * @throws Exception
     */
    public int executeBySql(String sql, MapSqlParameterSource parameterSource) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.MASTER_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.executeBySql(sql, parameterSource, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param parametersList
     * @return
     * @throws Exception
     */
    public int[] executeBatchBySql(String sql, List<?> parametersList) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.MASTER_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.executeBatchBySql(sql, parametersList, namedParameterJdbcTemplate);
    }
}