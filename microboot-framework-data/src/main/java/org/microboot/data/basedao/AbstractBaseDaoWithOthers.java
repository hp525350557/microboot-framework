package org.microboot.data.basedao;

import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
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
public abstract class AbstractBaseDaoWithOthers extends AbstractBaseDao {

    private final Logger logger = LogManager.getLogger(this.getClass());

    /**
     * @param templateName
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForListWithOthers(String templateName, Map<String, ?> parameters, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
        return this.queryForList(templateName, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param javaBean
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForListWithOthers(String templateName, Object javaBean, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForListWithOthers(templateName, parameters, dataBaseName);
    }

    /**
     * @param templateName
     * @param paramKey
     * @param paramValue
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForListWithOthers(String templateName, String paramKey, Object paramValue, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForListWithOthers(templateName, parameters, dataBaseName);
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
    public <T> List<T> queryForListWithOthers(String templateName, Map<String, ?> parameters, Class<T> clazz, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
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
    public <T> List<T> queryForListWithOthers(String templateName, Object javaBean, Class<T> clazz, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForListWithOthers(templateName, parameters, clazz, dataBaseName);
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
    public <T> List<T> queryForListWithOthers(String templateName, String paramKey, Object paramValue, Class<T> clazz, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForListWithOthers(templateName, parameters, clazz, dataBaseName);
    }

    /**
     * @param templateName
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMapWithOthers(String templateName, Map<String, ?> parameters, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
        return this.queryForMap(templateName, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param javaBean
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMapWithOthers(String templateName, Object javaBean, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForMapWithOthers(templateName, parameters, dataBaseName);
    }

    /**
     * @param templateName
     * @param paramKey
     * @param paramValue
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMapWithOthers(String templateName, String paramKey, Object paramValue, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForMapWithOthers(templateName, parameters, dataBaseName);
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
    public <T> T queryForObjectWithOthers(String templateName, Map<String, ?> parameters, Class<T> clazz, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
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
    public <T> T queryForObjectWithOthers(String templateName, Object javaBean, Class<T> clazz, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForObjectWithOthers(templateName, parameters, clazz, dataBaseName);
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
    public <T> T queryForObjectWithOthers(String templateName, String paramKey, Object paramValue, Class<T> clazz, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForObjectWithOthers(templateName, parameters, clazz, dataBaseName);
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
    public <T> T queryWithOthers(String templateName, Map<String, ?> parameters, String dataBaseName, ResultSetExtractor<T> rse) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
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
    public <T> T queryWithOthers(String templateName, Object javaBean, String dataBaseName, ResultSetExtractor<T> rse) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryWithOthers(templateName, parameters, dataBaseName, rse);
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
    public <T> T queryWithOthers(String templateName, String paramKey, Object paramValue, String dataBaseName, ResultSetExtractor<T> rse) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryWithOthers(templateName, parameters, dataBaseName, rse);
    }

    /**
     * @param templateName
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSetWithOthers(String templateName, Map<String, ?> parameters, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
        return this.queryForSqlRowSet(templateName, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param javaBean
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSetWithOthers(String templateName, Object javaBean, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForSqlRowSetWithOthers(templateName, parameters, dataBaseName);
    }

    /**
     * @param templateName
     * @param paramKey
     * @param paramValue
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSetWithOthers(String templateName, String paramKey, Object paramValue, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForSqlRowSetWithOthers(templateName, parameters, dataBaseName);
    }

    /**
     * @param paginationCount
     * @param pagination
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public Page paginationWithOthers(String paginationCount, String pagination, Map<String, ?> parameters, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
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
    public Page paginationWithOthers(String paginationCount, String pagination, Object javaBean, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.paginationWithOthers(paginationCount, pagination, parameters, dataBaseName);
    }

    /**
     * @param sql
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForListBySqlWithOthers(String sql, Map<String, ?> parameters, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
        return this.queryForListBySql(sql, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param javaBean
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForListBySqlWithOthers(String sql, Object javaBean, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForListBySqlWithOthers(sql, parameters, dataBaseName);
    }

    /**
     * @param sql
     * @param paramKey
     * @param paramValue
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForListBySqlWithOthers(String sql, String paramKey, Object paramValue, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForListBySqlWithOthers(sql, parameters, dataBaseName);
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
    public <T> List<T> queryForListBySqlWithOthers(String sql, Map<String, ?> parameters, Class<T> clazz, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
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
    public <T> List<T> queryForListBySqlWithOthers(String sql, Object javaBean, Class<T> clazz, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForListBySqlWithOthers(sql, parameters, clazz, dataBaseName);
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
    public <T> List<T> queryForListBySqlWithOthers(String sql, String paramKey, Object paramValue, Class<T> clazz, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForListBySqlWithOthers(sql, parameters, clazz, dataBaseName);
    }

    /**
     * @param sql
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMapBySqlWithOthers(String sql, Map<String, ?> parameters, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
        return this.queryForMapBySql(sql, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param javaBean
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMapBySqlWithOthers(String sql, Object javaBean, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForMapBySqlWithOthers(sql, parameters, dataBaseName);
    }

    /**
     * @param sql
     * @param paramKey
     * @param paramValue
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMapBySqlWithOthers(String sql, String paramKey, Object paramValue, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForMapBySqlWithOthers(sql, parameters, dataBaseName);
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
    public <T> T queryForObjectBySqlWithOthers(String sql, Map<String, ?> parameters, Class<T> clazz, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
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
    public <T> T queryForObjectBySqlWithOthers(String sql, Object javaBean, Class<T> clazz, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForObjectBySqlWithOthers(sql, parameters, clazz, dataBaseName);
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
    public <T> T queryForObjectBySqlWithOthers(String sql, String paramKey, Object paramValue, Class<T> clazz, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForObjectBySqlWithOthers(sql, parameters, clazz, dataBaseName);
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
    public <T> T queryBySqlWithOthers(String sql, Map<String, ?> parameters, String dataBaseName, ResultSetExtractor<T> rse) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
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
    public <T> T queryBySqlWithOthers(String sql, Object javaBean, String dataBaseName, ResultSetExtractor<T> rse) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryBySqlWithOthers(sql, parameters, dataBaseName, rse);
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
    public <T> T queryBySqlWithOthers(String sql, String paramKey, Object paramValue, String dataBaseName, ResultSetExtractor<T> rse) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryBySqlWithOthers(sql, parameters, dataBaseName, rse);
    }

    /**
     * @param sql
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSetBySqlWithOthers(String sql, Map<String, ?> parameters, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
        return this.queryForSqlRowSetBySql(sql, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param javaBean
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSetBySqlWithOthers(String sql, Object javaBean, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.queryForSqlRowSetBySqlWithOthers(sql, parameters, dataBaseName);
    }

    /**
     * @param sql
     * @param paramKey
     * @param paramValue
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSetBySqlWithOthers(String sql, String paramKey, Object paramValue, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForSqlRowSetBySqlWithOthers(sql, parameters, dataBaseName);
    }

    /**
     * @param paginationCountSql
     * @param paginationSql
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public Page paginationBySqlWithOthers(String paginationCountSql, String paginationSql, Map<String, ?> parameters, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
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
    public Page paginationBySqlWithOthers(String paginationCountSql, String paginationSql, Object javaBean, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.paginationBySqlWithOthers(paginationCountSql, paginationSql, parameters, dataBaseName);
    }

    /**
     * @param templateName
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeWithOthers(String templateName, Map<String, ?> parameters, String dataBaseName) throws Exception {
        /*
            不能复用execute*方法，因为getOrCreate方法会将当前namedParameterJdbcTemplate对象设置到ThreadLocal中
            这会导致读取从库数据时使用的数据源混乱
            ThreadLocal中只能保存主库的namedParameterJdbcTemplate对象
         */
        String sql = ApplicationContextHolder.getBean(TemplateResolver.class).processTemplate(templateName, parameters);
        return this.executeBySqlWithOthers(sql, parameters, dataBaseName);
    }

    /**
     * @param templateName
     * @param javaBean
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeWithOthers(String templateName, Object javaBean, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.executeWithOthers(templateName, parameters, dataBaseName);
    }

    /**
     * @param templateName
     * @param paramKey
     * @param paramValue
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeWithOthers(String templateName, String paramKey, Object paramValue, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.executeWithOthers(templateName, parameters, dataBaseName);
    }

    /**
     * @param templateName
     * @param parameterSource
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeWithOthers(String templateName, MapSqlParameterSource parameterSource, String dataBaseName) throws Exception {
        /*
            不能复用execute*方法，因为getOrCreate方法会将当前namedParameterJdbcTemplate对象设置到ThreadLocal中
            这会导致读取从库数据时使用的数据源混乱
            ThreadLocal中只能保存主库的namedParameterJdbcTemplate对象
         */
        Assert.notNull(parameterSource, "parameterSource must not be null");
        String sql = ApplicationContextHolder.getBean(TemplateResolver.class).processTemplate(templateName, parameterSource.getValues());
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
        return namedParameterJdbcTemplate.update(sql, parameterSource);
    }


    /**
     * @param templateName
     * @param parametersList
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int[] executeBatchWithOthers(String templateName, List<?> parametersList, String dataBaseName) throws Exception {
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
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
        return namedParameterJdbcTemplate.batchUpdate(sql, SqlParameterSourceUtils.createBatch(parametersList));
    }

    /**
     * @param sql
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeBySqlWithOthers(String sql, Map<String, ?> parameters, String dataBaseName) throws Exception {
        /*
            不能复用execute*方法，因为getOrCreate方法会将当前namedParameterJdbcTemplate对象设置到ThreadLocal中
            这会导致读取从库数据时使用的数据源混乱
            ThreadLocal中只能保存主库的namedParameterJdbcTemplate对象
         */
        logger.info(sql + " -> " + ConvertUtils.map2Json(parameters));
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
        return namedParameterJdbcTemplate.update(sql, parameters);
    }

    /**
     * @param sql
     * @param javaBean
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeBySqlWithOthers(String sql, Object javaBean, String dataBaseName) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.executeBySqlWithOthers(sql, parameters, dataBaseName);
    }

    /**
     * @param sql
     * @param paramKey
     * @param paramValue
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeBySqlWithOthers(String sql, String paramKey, Object paramValue, String dataBaseName) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.executeBySqlWithOthers(sql, parameters, dataBaseName);
    }

    /**
     * @param sql
     * @param parameterSource
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeBySqlWithOthers(String sql, MapSqlParameterSource parameterSource, String dataBaseName) throws Exception {
        /*
            不能复用execute*方法，因为getOrCreate方法会将当前namedParameterJdbcTemplate对象设置到ThreadLocal中
            这会导致读取从库数据时使用的数据源混乱
            ThreadLocal中只能保存主库的namedParameterJdbcTemplate对象
         */
        Assert.notNull(parameterSource, "parameterSource must not be null");
        logger.info(sql + " -> " + ConvertUtils.object2Json(parameterSource));
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
        return namedParameterJdbcTemplate.update(sql, parameterSource);
    }

    /**
     * @param sql
     * @param parametersList
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int[] executeBatchBySqlWithOthers(String sql, List<?> parametersList, String dataBaseName) throws Exception {
        /*
            不能复用execute*方法，因为getOrCreate方法会将当前namedParameterJdbcTemplate对象设置到ThreadLocal中
            这会导致读取从库数据时使用的数据源混乱
            ThreadLocal中只能保存主库的namedParameterJdbcTemplate对象
         */
        logger.info(sql + " -> " + ConvertUtils.listMap2Json(parametersList));
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
        return namedParameterJdbcTemplate.batchUpdate(sql, SqlParameterSourceUtils.createBatch(parametersList));
    }

    /**
     * @param dataBaseName（其他库）
     * @return
     * @throws Exception
     */
    protected NamedParameterJdbcTemplate getNamedParameterJdbcTemplateWithOthers(String dataBaseName) throws Exception {
        if (StringUtils.isBlank(dataBaseName)) {
            throw new IllegalArgumentException("dataBaseName must not be null");
        }
        Map<String, NamedParameterJdbcTemplate> namedParameterJdbcTemplateMap = DataContainer.othersMap;
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = namedParameterJdbcTemplateMap.getOrDefault(dataBaseName, null);
        if (namedParameterJdbcTemplate == null) {
            throw new SQLException("no available connections were found");
        }
        return namedParameterJdbcTemplate;
    }
}