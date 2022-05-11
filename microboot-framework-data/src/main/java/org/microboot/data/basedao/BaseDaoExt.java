package org.microboot.data.basedao;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.constant.Constant;
import org.microboot.core.utils.ConvertUtils;
import org.microboot.core.utils.LoggerUtils;
import org.microboot.data.container.DataContainer;
import org.microboot.data.resolver.TemplateResolver;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author 胡鹏
 */
public abstract class BaseDaoExt extends TransmittableThreadLocal<NamedParameterJdbcTemplate> {

    private final Logger logger = LogManager.getLogger(this.getClass());

    /**
     * @param templateName
     * @param parameters
     * @param rse
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T query(String templateName, Map<String, ?> parameters, ResultSetExtractor<T> rse) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves();
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.query(templateName, parameters, rse);
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
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves();
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryBySql(sql, parameters, rse);
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryWithSlaves(templateName, parameters, dataBaseName, rse);
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryBySqlWithSlaves(sql, parameters, dataBaseName, rse);
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryWithOthers(templateName, parameters, dataBaseName, rse);
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryBySqlWithOthers(sql, parameters, dataBaseName, rse);
    }

    /**
     * @param templateName
     * @param parameters
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSet(String templateName, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves();
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForSqlRowSet(templateName, parameters);
    }

    /**
     * @param sql
     * @param parameters
     * @return
     * @throws Exception
     */
    public SqlRowSet queryForSqlRowSetBySql(String sql, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves();
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForSqlRowSetBySql(sql, parameters);
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForSqlRowSetWithSlaves(templateName, parameters, dataBaseName);
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForSqlRowSetBySqlWithSlaves(sql, parameters, dataBaseName);
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForSqlRowSetWithOthers(templateName, parameters, dataBaseName);
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForSqlRowSetBySqlWithOthers(sql, parameters, dataBaseName);
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
     * @param templateName
     * @param parameterSource
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeWithSlaves(String templateName, MapSqlParameterSource parameterSource, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return this.execute(templateName, parameterSource, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param parameterSource
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeBySqlWithSlaves(String sql, MapSqlParameterSource parameterSource, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return this.executeBySql(sql, parameterSource, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param parameterSource
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeWithOthers(String templateName, MapSqlParameterSource parameterSource, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
        return this.execute(templateName, parameterSource, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param parameterSource
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeBySqlWithOthers(String sql, MapSqlParameterSource parameterSource, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
        return this.executeBySql(sql, parameterSource, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param parameters
     * @param namedParameterJdbcTemplate
     * @param rse
     * @param <T>
     * @return
     * @throws Exception
     */
    private <T> T query(String templateName, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate, ResultSetExtractor<T> rse) throws Exception {
        String sql = ApplicationContextHolder.getBean(TemplateResolver.class).processTemplate(templateName, parameters);
        return this.queryBySql(sql, parameters, namedParameterJdbcTemplate, rse);
    }

    /**
     * @param sql
     * @param parameters
     * @param namedParameterJdbcTemplate
     * @param rse
     * @param <T>
     * @return
     * @throws Exception
     */
    private <T> T queryBySql(String sql, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate, ResultSetExtractor<T> rse) throws Exception {
        /*
         * 这里捕捉EmptyResultDataAccessException异常是因为NamedParameterJdbcTemplate查询出null值时会报这个异常
         */
        try {
            logger.info(sql + " -> " + ConvertUtils.map2Json(parameters));
            return namedParameterJdbcTemplate.query(sql, parameters, rse);
        } catch (EmptyResultDataAccessException e) {
            LoggerUtils.warn(logger, e);
            return null;
        }
    }

    /**
     * @param templateName
     * @param parameters
     * @param namedParameterJdbcTemplate
     * @return
     * @throws Exception
     */
    private SqlRowSet queryForSqlRowSet(String templateName, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
        String sql = ApplicationContextHolder.getBean(TemplateResolver.class).processTemplate(templateName, parameters);
        return this.queryForSqlRowSetBySql(sql, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param parameters
     * @param namedParameterJdbcTemplate
     * @return
     * @throws Exception
     */
    private SqlRowSet queryForSqlRowSetBySql(String sql, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
        /*
         * 这里捕捉EmptyResultDataAccessException异常是因为NamedParameterJdbcTemplate查询出null值时会报这个异常
         */
        try {
            logger.info(sql + " -> " + ConvertUtils.map2Json(parameters));
            return namedParameterJdbcTemplate.queryForRowSet(sql, parameters);
        } catch (EmptyResultDataAccessException e) {
            LoggerUtils.warn(logger, e);
            return null;
        }
    }

    /**
     * @param templateName
     * @param parameterSource
     * @param namedParameterJdbcTemplate
     * @return
     * @throws Exception
     */
    private int execute(String templateName, MapSqlParameterSource parameterSource, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
        String sql = ApplicationContextHolder.getBean(TemplateResolver.class).processTemplate(templateName, parameterSource.getValues());
        return this.executeBySql(sql, parameterSource, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param parameterSource
     * @param namedParameterJdbcTemplate
     * @return
     * @throws Exception
     */
    private int executeBySql(String sql, MapSqlParameterSource parameterSource, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
        logger.info(sql + " -> " + ConvertUtils.object2Json(parameterSource));
        this.getOrCreate(namedParameterJdbcTemplate);
        return namedParameterJdbcTemplate.update(sql, parameterSource);
    }

    /**
     * @param namedParameterJdbcTemplates
     * @return
     */
    protected NamedParameterJdbcTemplate getOrCreate(NamedParameterJdbcTemplate... namedParameterJdbcTemplates) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.get();
        if (namedParameterJdbcTemplate != null) {
            logger.info("读Thread：" + Thread.currentThread() + "=======>" + namedParameterJdbcTemplate);
            return namedParameterJdbcTemplate;
        }
        if (ArrayUtils.isNotEmpty(namedParameterJdbcTemplates)) {
            this.set(namedParameterJdbcTemplates[0]);
            logger.info("写Thread：" + Thread.currentThread() + "=======>" + namedParameterJdbcTemplates[0]);
        }
        return this.get();
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

    /**
     * @param dataBaseName（其他库）
     * @return
     * @throws Exception
     */
    protected NamedParameterJdbcTemplate getNamedParameterJdbcTemplateWithOthers(String dataBaseName) throws Exception {
        if (StringUtils.isBlank(dataBaseName)) {
            throw new IllegalArgumentException("<dataBaseName> cannot be null");
        }
        Map<String, NamedParameterJdbcTemplate> namedParameterJdbcTemplateMap = DataContainer.othersMap;
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = namedParameterJdbcTemplateMap.getOrDefault(dataBaseName, null);
        if (namedParameterJdbcTemplate == null) {
            throw new SQLException("no available connections were found");
        }
        return namedParameterJdbcTemplate;
    }

    /**
     * 从namedParameterJdbcTemplateMap中随机获取一个NamedParameterJdbcTemplate
     * 并依据backoff判断是否处于退避阶段
     *
     * @param namedParameterJdbcTemplateMap
     * @return
     */
    protected NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(Map<String, NamedParameterJdbcTemplate> namedParameterJdbcTemplateMap) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate;
        //总连接数
        int size = namedParameterJdbcTemplateMap.size();
        //总连接数为0表示没有可用连接
        if (size == 0) {
            return null;
        }
        //随机下标
        // ThreadLocalRandom比Random性能更高
        // java7在所有情形下都更推荐使用ThreadLocalRandom，它向下兼容已有的代码且运营成本更低
        int index = ThreadLocalRandom.current().nextInt(size);
        //所有连接名（new String[0]用来指定数组类型，toArray方法的参数是泛型）
        String[] names = namedParameterJdbcTemplateMap.keySet().toArray(new String[0]);
        if (index >= names.length) {
            /*
                在主从读写分离模式下，如果从库连接异常发生退避，有可能出现index >= names.length造成names[index]下标溢出
                因此递归重新获取namedParameterJdbcTemplate对象
             */
            namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplate(namedParameterJdbcTemplateMap);
        } else {
            //提取连接名
            String name = names[index];
            //此处获取的连接名有可能已经被退避了，可能会获取到null值
            namedParameterJdbcTemplate = namedParameterJdbcTemplateMap.getOrDefault(name, null);
            //确保最终拿到null值是因为namedParameterJdbcTemplateMap没有可用连接了
            if (namedParameterJdbcTemplate == null && namedParameterJdbcTemplateMap.size() > 0) {
                namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplate(namedParameterJdbcTemplateMap);
            }
        }
        return namedParameterJdbcTemplate;
    }
}