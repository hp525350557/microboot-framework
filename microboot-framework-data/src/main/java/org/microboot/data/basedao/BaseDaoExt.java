package org.microboot.data.basedao;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.utils.ConvertUtils;
import org.microboot.core.utils.LoggerUtils;
import org.microboot.data.resolver.TemplateResolver;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.Map;

/**
 * @author 胡鹏
 */
public class BaseDaoExt extends BaseDao {

    private final Logger logger = LogManager.getLogger(this.getClass());

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
}