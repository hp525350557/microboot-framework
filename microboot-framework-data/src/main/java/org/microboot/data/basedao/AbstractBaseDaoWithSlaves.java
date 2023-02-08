package org.microboot.data.basedao;

import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.entity.Page;
import org.microboot.core.utils.ConvertUtils;
import org.microboot.data.container.DataContainer;
import org.microboot.data.resolver.TemplateResolver;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.util.Assert;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author 胡鹏
 */
public abstract class AbstractBaseDaoWithSlaves extends AbstractBaseDaoWithOthers {

    private final Logger logger = LogManager.getLogger(this.getClass());

    /**
     * @param templateName
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForListWithSlaves(String templateName, Map<String, ?> parameters, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return this.queryForList(templateName, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param javaBean
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForListWithSlaves(String templateName, Object javaBean, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForListWithSlaves(templateName, parameters, dataBaseName);
    }

    /**
     * @param templateName
     * @param paramKey
     * @param paramValue
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForListWithSlaves(String templateName, String paramKey, Object paramValue, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForListWithSlaves(templateName, parameters, dataBaseName);
    }

    /**
     * @param templateName
     * @param parameters
     * @param clazz
     * @param dataBaseName
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> queryForListWithSlaves(String templateName, Map<String, ?> parameters, Class<T> clazz, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return this.queryForList(templateName, parameters, clazz, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param javaBean
     * @param clazz
     * @param dataBaseName
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> queryForListWithSlaves(String templateName, Object javaBean, Class<T> clazz, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForListWithSlaves(templateName, parameters, clazz, dataBaseName);
    }

    /**
     * @param templateName
     * @param paramKey
     * @param paramValue
     * @param clazz
     * @param dataBaseName
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> queryForListWithSlaves(String templateName, String paramKey, Object paramValue, Class<T> clazz, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForListWithSlaves(templateName, parameters, clazz, dataBaseName);
    }

    /**
     * @param templateName
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMapWithSlaves(String templateName, Map<String, ?> parameters, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return this.queryForMap(templateName, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param javaBean
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMapWithSlaves(String templateName, Object javaBean, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForMapWithSlaves(templateName, parameters, dataBaseName);
    }

    /**
     * @param templateName
     * @param paramKey
     * @param paramValue
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMapWithSlaves(String templateName, String paramKey, Object paramValue, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForMapWithSlaves(templateName, parameters, dataBaseName);
    }

    /**
     * @param templateName
     * @param parameters
     * @param clazz
     * @param dataBaseName
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryForObjectWithSlaves(String templateName, Map<String, ?> parameters, Class<T> clazz, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return this.queryForObject(templateName, parameters, clazz, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param javaBean
     * @param clazz
     * @param dataBaseName
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryForObjectWithSlaves(String templateName, Object javaBean, Class<T> clazz, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForObjectWithSlaves(templateName, parameters, clazz, dataBaseName);
    }

    /**
     * @param templateName
     * @param paramKey
     * @param paramValue
     * @param clazz
     * @param dataBaseName
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryForObjectWithSlaves(String templateName, String paramKey, Object paramValue, Class<T> clazz, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForObjectWithSlaves(templateName, parameters, clazz, dataBaseName);
    }

    /**
     * @param templateName
     * @param parameters
     * @param dataBaseName
     * @param rse
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryWithSlaves(String templateName, Map<String, ?> parameters, String dataBaseName, ResultSetExtractor<T> rse) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return this.query(templateName, parameters, namedParameterJdbcTemplate, rse);
    }

    /**
     * @param templateName
     * @param javaBean
     * @param dataBaseName
     * @param rse
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryWithSlaves(String templateName, Object javaBean, String dataBaseName, ResultSetExtractor<T> rse) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryWithSlaves(templateName, parameters, dataBaseName, rse);
    }

    /**
     * @param templateName
     * @param paramKey
     * @param paramValue
     * @param dataBaseName
     * @param rse
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryWithSlaves(String templateName, String paramKey, Object paramValue, String dataBaseName, ResultSetExtractor<T> rse) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryWithSlaves(templateName, parameters, dataBaseName, rse);
    }

    /**
     * @param templateName
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSetWithSlaves(String templateName, Map<String, ?> parameters, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return this.queryForSqlRowSet(templateName, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param javaBean
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSetWithSlaves(String templateName, Object javaBean, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForSqlRowSetWithSlaves(templateName, parameters, dataBaseName);
    }

    /**
     * @param templateName
     * @param paramKey
     * @param paramValue
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSetWithSlaves(String templateName, String paramKey, Object paramValue, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForSqlRowSetWithSlaves(templateName, parameters, dataBaseName);
    }

    /**
     * @param paginationCount
     * @param pagination
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public Page paginationWithSlaves(String paginationCount, String pagination, Map<String, ?> parameters, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return this.pagination(paginationCount, pagination, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param paginationCount
     * @param pagination
     * @param javaBean
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public Page paginationWithSlaves(String paginationCount, String pagination, Object javaBean, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.paginationWithSlaves(paginationCount, pagination, parameters, dataBaseName);
    }

    /**
     * @param sql
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForListBySqlWithSlaves(String sql, Map<String, ?> parameters, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return this.queryForListBySql(sql, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param javaBean
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForListBySqlWithSlaves(String sql, Object javaBean, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForListBySqlWithSlaves(sql, parameters, dataBaseName);
    }

    /**
     * @param sql
     * @param paramKey
     * @param paramValue
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForListBySqlWithSlaves(String sql, String paramKey, Object paramValue, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForListBySqlWithSlaves(sql, parameters, dataBaseName);
    }

    /**
     * @param sql
     * @param parameters
     * @param clazz
     * @param dataBaseName
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> queryForListBySqlWithSlaves(String sql, Map<String, ?> parameters, Class<T> clazz, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return this.queryForListBySql(sql, parameters, clazz, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param javaBean
     * @param clazz
     * @param dataBaseName
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> queryForListBySqlWithSlaves(String sql, Object javaBean, Class<T> clazz, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForListBySqlWithSlaves(sql, parameters, clazz, dataBaseName);
    }

    /**
     * @param sql
     * @param paramKey
     * @param paramValue
     * @param clazz
     * @param dataBaseName
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> queryForListBySqlWithSlaves(String sql, String paramKey, Object paramValue, Class<T> clazz, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForListBySqlWithSlaves(sql, parameters, clazz, dataBaseName);
    }

    /**
     * @param sql
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMapBySqlWithSlaves(String sql, Map<String, ?> parameters, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return this.queryForMapBySql(sql, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param javaBean
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMapBySqlWithSlaves(String sql, Object javaBean, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForMapBySqlWithSlaves(sql, parameters, dataBaseName);
    }

    /**
     * @param sql
     * @param paramKey
     * @param paramValue
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMapBySqlWithSlaves(String sql, String paramKey, Object paramValue, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForMapBySqlWithSlaves(sql, parameters, dataBaseName);
    }

    /**
     * @param sql
     * @param parameters
     * @param clazz
     * @param dataBaseName
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryForObjectBySqlWithSlaves(String sql, Map<String, ?> parameters, Class<T> clazz, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return this.queryForObjectBySql(sql, parameters, clazz, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param javaBean
     * @param clazz
     * @param dataBaseName
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryForObjectBySqlWithSlaves(String sql, Object javaBean, Class<T> clazz, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForObjectBySqlWithSlaves(sql, parameters, clazz, dataBaseName);
    }

    /**
     * @param sql
     * @param paramKey
     * @param paramValue
     * @param clazz
     * @param dataBaseName
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryForObjectBySqlWithSlaves(String sql, String paramKey, Object paramValue, Class<T> clazz, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForObjectBySqlWithSlaves(sql, parameters, clazz, dataBaseName);
    }

    /**
     * @param sql
     * @param parameters
     * @param dataBaseName
     * @param rse
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryBySqlWithSlaves(String sql, Map<String, ?> parameters, String dataBaseName, ResultSetExtractor<T> rse) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return this.queryBySql(sql, parameters, namedParameterJdbcTemplate, rse);
    }

    /**
     * @param sql
     * @param javaBean
     * @param dataBaseName
     * @param rse
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryBySqlWithSlaves(String sql, Object javaBean, String dataBaseName, ResultSetExtractor<T> rse) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryBySqlWithSlaves(sql, parameters, dataBaseName, rse);
    }

    /**
     * @param sql
     * @param paramKey
     * @param paramValue
     * @param dataBaseName
     * @param rse
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T queryBySqlWithSlaves(String sql, String paramKey, Object paramValue, String dataBaseName, ResultSetExtractor<T> rse) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryBySqlWithSlaves(sql, parameters, dataBaseName, rse);
    }

    /**
     * @param sql
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSetBySqlWithSlaves(String sql, Map<String, ?> parameters, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return this.queryForSqlRowSetBySql(sql, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param javaBean
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSetBySqlWithSlaves(String sql, Object javaBean, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForSqlRowSetBySqlWithSlaves(sql, parameters, dataBaseName);
    }

    /**
     * @param sql
     * @param paramKey
     * @param paramValue
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSetBySqlWithSlaves(String sql, String paramKey, Object paramValue, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForSqlRowSetBySqlWithSlaves(sql, parameters, dataBaseName);
    }

    /**
     * @param paginationCountSql
     * @param paginationSql
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public Page paginationBySqlWithSlaves(String paginationCountSql, String paginationSql, Map<String, ?> parameters, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return this.paginationBySql(paginationCountSql, paginationSql, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param paginationCountSql
     * @param paginationSql
     * @param javaBean
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public Page paginationBySqlWithSlaves(String paginationCountSql, String paginationSql, Object javaBean, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.paginationBySqlWithSlaves(paginationCountSql, paginationSql, parameters, dataBaseName);
    }

    /**
     * @param templateName
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeWithSlaves(String templateName, Map<String, ?> parameters, String dataBaseName) throws Exception {
        /*
            不能复用execute*方法，因为getOrCreate方法会将当前namedParameterJdbcTemplate对象设置到ThreadLocal中
            这会导致读取从库数据时使用的数据源混乱
            ThreadLocal中只能保存主库的namedParameterJdbcTemplate对象
         */
        String sql = ApplicationContextHolder.getBean(TemplateResolver.class).processTemplate(templateName, parameters);
        return this.executeBySqlWithSlaves(sql, parameters, dataBaseName);
    }

    /**
     * @param templateName
     * @param javaBean
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeWithSlaves(String templateName, Object javaBean, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.executeWithSlaves(templateName, parameters, dataBaseName);
    }

    /**
     * @param templateName
     * @param paramKey
     * @param paramValue
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeWithSlaves(String templateName, String paramKey, Object paramValue, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.executeWithSlaves(templateName, parameters, dataBaseName);
    }

    /**
     * @param templateName
     * @param parameterSource
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeWithSlaves(String templateName, MapSqlParameterSource parameterSource, String dataBaseName) throws Exception {
        /*
            不能复用execute*方法，因为getOrCreate方法会将当前namedParameterJdbcTemplate对象设置到ThreadLocal中
            这会导致读取从库数据时使用的数据源混乱
            ThreadLocal中只能保存主库的namedParameterJdbcTemplate对象
         */
        Assert.notNull(parameterSource, "parameterSource must not be null");
        String sql = ApplicationContextHolder.getBean(TemplateResolver.class).processTemplate(templateName, parameterSource.getValues());
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return namedParameterJdbcTemplate.update(sql, parameterSource);
    }

    /**
     * @param templateName
     * @param parametersList
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int[] executeBatchWithSlaves(String templateName, List<?> parametersList, String dataBaseName) throws Exception {
        /*
            不能复用execute*方法，因为getOrCreate方法会将当前namedParameterJdbcTemplate对象设置到ThreadLocal中
            这会导致读取从库数据时使用的数据源混乱
            ThreadLocal中只能保存主库的namedParameterJdbcTemplate对象
         */
        if (CollectionUtils.isEmpty(parametersList)) {
            return new int[]{0};
        }
        //得到sql模板
        String sql = ApplicationContextHolder.getBean(TemplateResolver.class).processTemplate(templateName, null);
        logger.info(sql + " -> " + ConvertUtils.listMap2Json(parametersList));
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return namedParameterJdbcTemplate.batchUpdate(sql, SqlParameterSourceUtils.createBatch(parametersList));
    }

    /**
     * @param sql
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeBySqlWithSlaves(String sql, Map<String, ?> parameters, String dataBaseName) throws Exception {
        /*
            不能复用execute*方法，因为getOrCreate方法会将当前namedParameterJdbcTemplate对象设置到ThreadLocal中
            这会导致读取从库数据时使用的数据源混乱
            ThreadLocal中只能保存主库的namedParameterJdbcTemplate对象
         */
        logger.info(sql + " -> " + ConvertUtils.map2Json(parameters));
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return namedParameterJdbcTemplate.update(sql, parameters);
    }

    /**
     * @param sql
     * @param javaBean
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeBySqlWithSlaves(String sql, Object javaBean, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.executeBySqlWithSlaves(sql, parameters, dataBaseName);
    }

    /**
     * @param sql
     * @param paramKey
     * @param paramValue
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeBySqlWithSlaves(String sql, String paramKey, Object paramValue, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.executeBySqlWithSlaves(sql, parameters, dataBaseName);
    }

    /**
     * @param sql
     * @param parameterSource
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeBySqlWithSlaves(String sql, MapSqlParameterSource parameterSource, String dataBaseName) throws Exception {
        /*
            不能复用execute*方法，因为getOrCreate方法会将当前namedParameterJdbcTemplate对象设置到ThreadLocal中
            这会导致读取从库数据时使用的数据源混乱
            ThreadLocal中只能保存主库的namedParameterJdbcTemplate对象
         */
        Assert.notNull(parameterSource, "parameterSource must not be null");
        logger.info(sql + " -> " + ConvertUtils.object2Json(parameterSource));
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return namedParameterJdbcTemplate.update(sql, parameterSource);
    }

    /**
     * @param sql
     * @param parametersList
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int[] executeBatchBySqlWithSlaves(String sql, List<?> parametersList, String dataBaseName) throws Exception {
        /*
            不能复用execute*方法，因为getOrCreate方法会将当前namedParameterJdbcTemplate对象设置到ThreadLocal中
            这会导致读取从库数据时使用的数据源混乱
            ThreadLocal中只能保存主库的namedParameterJdbcTemplate对象
         */
        logger.info(sql + " -> " + ConvertUtils.listMap2Json(parametersList));
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return namedParameterJdbcTemplate.batchUpdate(sql, SqlParameterSourceUtils.createBatch(parametersList));
    }

    /**
     * @param dataBaseName（从库）
     * @return
     * @throws Exception
     */
    protected NamedParameterJdbcTemplate getNamedParameterJdbcTemplateWithSlaves(String... dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate;
        Map<String, NamedParameterJdbcTemplate> namedParameterJdbcTemplateMap = DataContainer.slavesMap;
        if (ArrayUtils.isNotEmpty(dataBaseName)) {
            String name = dataBaseName[0];
            namedParameterJdbcTemplate = namedParameterJdbcTemplateMap.getOrDefault(name, null);
        } else {
            namedParameterJdbcTemplate = this.getOrCreate();
            if (namedParameterJdbcTemplate != null) {
                return namedParameterJdbcTemplate;
            }
            namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplate(namedParameterJdbcTemplateMap);
        }
        if (namedParameterJdbcTemplate == null) {
            throw new SQLException("no available connections were found");
        }
        return namedParameterJdbcTemplate;
    }
}