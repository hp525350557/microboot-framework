package org.microboot.data.basedao;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.constant.Constant;
import org.microboot.core.entity.Page;
import org.microboot.core.utils.ConvertUtils;
import org.microboot.core.utils.LoggerUtils;
import org.microboot.data.container.DataContainer;
import org.microboot.data.resolver.TemplateResolver;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author 胡鹏
 */
public class BaseDao extends TransmittableThreadLocal<NamedParameterJdbcTemplate> {

    private final Logger logger = LogManager.getLogger(this.getClass());

    /**
     * @param templateName
     * @param parameters
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryForList(String templateName, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves();
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
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
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves();
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
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
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves();
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
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
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves();
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForObject(templateName, parameters, clazz);
    }

    /**
     * @param paginationCount
     * @param pagination
     * @param parameters
     * @return
     * @throws Exception
     */
    public Page pagination(String paginationCount, String pagination, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves();
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
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves();
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
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
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves();
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
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
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves();
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
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
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves();
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForObjectBySql(sql, parameters, clazz);
    }

    /**
     * @param paginationCountSql
     * @param paginationSql
     * @param parameters
     * @return
     * @throws Exception
     */
    public Page paginationBySql(String paginationCountSql, String paginationSql, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves();
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForObjectWithSlaves(templateName, parameters, clazz, dataBaseName);
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForObjectBySqlWithSlaves(sql, parameters, clazz, dataBaseName);
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForObjectWithOthers(templateName, parameters, clazz, dataBaseName);
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.queryForObjectBySqlWithOthers(sql, parameters, clazz, dataBaseName);
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.execute(templateName, parameters);
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.executeBySql(sql, parameters);
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

    /**
     * @param templateName
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeWithSlaves(String templateName, Map<String, ?> parameters, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return this.execute(templateName, parameters, namedParameterJdbcTemplate);
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.executeWithSlaves(templateName, parameters, dataBaseName);
    }

    /**
     * @param templateName
     * @param parametersList
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int[] executeBatchWithSlaves(String templateName, List<?> parametersList, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return this.executeBatch(templateName, parametersList, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeBySqlWithSlaves(String sql, Map<String, ?> parameters, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return this.executeBySql(sql, parameters, namedParameterJdbcTemplate);
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.executeBySqlWithSlaves(sql, parameters, dataBaseName);
    }

    /**
     * @param sql
     * @param parametersList
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int[] executeBatchBySqlWithSlaves(String sql, List<?> parametersList, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithSlaves(dataBaseName);
        return this.executeBatchBySql(sql, parametersList, namedParameterJdbcTemplate);
    }


    /**
     * @param templateName
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeWithOthers(String templateName, Map<String, ?> parameters, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
        return this.execute(templateName, parameters, namedParameterJdbcTemplate);
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.executeWithOthers(templateName, parameters, dataBaseName);
    }

    /**
     * @param templateName
     * @param parametersList
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int[] executeBatchWithOthers(String templateName, List<?> parametersList, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
        return this.executeBatch(templateName, parametersList, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param parameters
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int executeBySqlWithOthers(String sql, Map<String, ?> parameters, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
        return this.executeBySql(sql, parameters, namedParameterJdbcTemplate);
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
            throw new IllegalArgumentException("<paramKey> cannot be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.executeBySqlWithOthers(sql, parameters, dataBaseName);
    }

    /**
     * @param sql
     * @param parametersList
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public int[] executeBatchBySqlWithOthers(String sql, List<?> parametersList, String dataBaseName) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplateWithOthers(dataBaseName);
        return this.executeBatchBySql(sql, parametersList, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param parameters
     * @param namedParameterJdbcTemplate
     * @return
     * @throws Exception
     */
    private List<Map<String, Object>> queryForList(String templateName, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
        String sql = ApplicationContextHolder.getBean(TemplateResolver.class).processTemplate(templateName, parameters);
        return this.queryForListBySql(sql, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param parameters
     * @param clazz
     * @param namedParameterJdbcTemplate
     * @param <T>
     * @return
     * @throws Exception
     */
    private <T> List<T> queryForList(String templateName, Map<String, ?> parameters, Class<T> clazz, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
        String sql = ApplicationContextHolder.getBean(TemplateResolver.class).processTemplate(templateName, parameters);
        return this.queryForListBySql(sql, parameters, clazz, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param parameters
     * @param namedParameterJdbcTemplate
     * @return
     * @throws Exception
     */
    private Map<String, Object> queryForMap(String templateName, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
        String sql = ApplicationContextHolder.getBean(TemplateResolver.class).processTemplate(templateName, parameters);
        return this.queryForMapBySql(sql, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param parameters
     * @param clazz
     * @param namedParameterJdbcTemplate
     * @param <T>
     * @return
     * @throws Exception
     */
    private <T> T queryForObject(String templateName, Map<String, ?> parameters, Class<T> clazz, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
        String sql = ApplicationContextHolder.getBean(TemplateResolver.class).processTemplate(templateName, parameters);
        return this.queryForObjectBySql(sql, parameters, clazz, namedParameterJdbcTemplate);
    }

    /**
     * @param paginationCount
     * @param pagination
     * @param parameters
     * @param namedParameterJdbcTemplate
     * @return
     * @throws Exception
     */
    private Page pagination(String paginationCount, String pagination, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
        Map<String, Object> newParameters = Maps.newHashMap(parameters);
        //总记录数sql
        String paginationCountSql = ApplicationContextHolder.getBean(TemplateResolver.class).processTemplate(paginationCount, newParameters);
        //分页查询sql
        String paginationSql = ApplicationContextHolder.getBean(TemplateResolver.class).processTemplate(pagination, newParameters);
        return this.getPage(paginationCountSql, paginationSql, newParameters, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param parameters
     * @param namedParameterJdbcTemplate
     * @return
     * @throws Exception
     */
    private List<Map<String, Object>> queryForListBySql(String sql, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
        /*
         * 这里捕捉EmptyResultDataAccessException异常是因为NamedParameterJdbcTemplate查询出null值时会报这个异常
         */
        try {
            logger.info(sql + " -> " + ConvertUtils.map2Json(parameters));
            return namedParameterJdbcTemplate.queryForList(sql, parameters);
        } catch (EmptyResultDataAccessException e) {
            LoggerUtils.warn(logger, e);
            return null;
        }
    }

    /**
     * @param sql
     * @param parameters
     * @param clazz
     * @param namedParameterJdbcTemplate
     * @param <T>
     * @return
     * @throws Exception
     */
    private <T> List<T> queryForListBySql(String sql, Map<String, ?> parameters, Class<T> clazz, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
        /*
         * 这里捕捉EmptyResultDataAccessException异常是因为NamedParameterJdbcTemplate查询出null值时会报这个异常
         */
        try {
            logger.info(sql + " -> " + ConvertUtils.map2Json(parameters));
            return namedParameterJdbcTemplate.query(sql, parameters, new BeanPropertyRowMapper<>(clazz));
        } catch (EmptyResultDataAccessException e) {
            LoggerUtils.warn(logger, e);
            return null;
        }
    }

    /**
     * @param sql
     * @param parameters
     * @param namedParameterJdbcTemplate
     * @return
     * @throws Exception
     */
    private Map<String, Object> queryForMapBySql(String sql, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
        /*
         * 这里捕捉EmptyResultDataAccessException异常是因为NamedParameterJdbcTemplate查询出null值时会报这个异常
         */
        try {
            logger.info(sql + " -> " + ConvertUtils.map2Json(parameters));
            return namedParameterJdbcTemplate.queryForMap(sql, parameters);
        } catch (EmptyResultDataAccessException e) {
            LoggerUtils.warn(logger, e);
            return null;
        }
    }

    /**
     * @param sql
     * @param parameters
     * @param clazz
     * @param namedParameterJdbcTemplate
     * @param <T>
     * @return
     * @throws Exception
     */
    private <T> T queryForObjectBySql(String sql, Map<String, ?> parameters, Class<T> clazz, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
        /*
         * 这里捕捉EmptyResultDataAccessException异常是因为NamedParameterJdbcTemplate查询出null值时会报这个异常
         */
        try {
            logger.info(sql + " -> " + ConvertUtils.map2Json(parameters));
            return namedParameterJdbcTemplate.queryForObject(sql, parameters, new BeanPropertyRowMapper<>(clazz));
        } catch (EmptyResultDataAccessException e) {
            LoggerUtils.warn(logger, e);
            return null;
        }
    }

    /**
     * @param paginationCountSql
     * @param paginationSql
     * @param parameters
     * @param namedParameterJdbcTemplate
     * @return
     * @throws Exception
     */
    private Page paginationBySql(String paginationCountSql, String paginationSql, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
        Map<String, Object> newParameters = Maps.newHashMap(parameters);
        return this.getPage(paginationCountSql, paginationSql, newParameters, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param parameters
     * @param namedParameterJdbcTemplate
     * @return
     * @throws Exception
     */
    private int execute(String templateName, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
        String sql = ApplicationContextHolder.getBean(TemplateResolver.class).processTemplate(templateName, parameters);
        return this.executeBySql(sql, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param parametersList
     * @param namedParameterJdbcTemplate
     * @return
     * @throws Exception
     */
    private int[] executeBatch(String templateName, List<?> parametersList, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
        if (CollectionUtils.isEmpty(parametersList)) {
            return new int[]{0};
        }
        //得到sql模板
        String sql = ApplicationContextHolder.getBean(TemplateResolver.class).processTemplate(templateName, null);
        return this.executeBatchBySql(sql, parametersList, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param parameters
     * @param namedParameterJdbcTemplate
     * @return
     */
    private int executeBySql(String sql, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        logger.info(sql + " -> " + ConvertUtils.map2Json(parameters));
        this.getOrCreate(namedParameterJdbcTemplate);
        return namedParameterJdbcTemplate.update(sql, parameters);
    }

    /**
     * @param sql
     * @param parametersList
     * @param namedParameterJdbcTemplate
     * @return
     */
    private int[] executeBatchBySql(String sql, List<?> parametersList, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        logger.info(sql + " -> " + ConvertUtils.listMap2Json(parametersList));
        this.getOrCreate(namedParameterJdbcTemplate);
        return namedParameterJdbcTemplate.batchUpdate(sql, SqlParameterSourceUtils.createBatch(parametersList));
    }

    /**
     * @param paginationCountSql
     * @param paginationSql
     * @param parameters
     * @param namedParameterJdbcTemplate
     * @return
     */
    private Page getPage(String paginationCountSql, String paginationSql, Map<String, Object> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        //查询总记录数
        logger.info(paginationCountSql + " " + ConvertUtils.map2Json(parameters));
        Map<String, Object> totalMap = namedParameterJdbcTemplate.queryForMap(paginationCountSql, parameters);
        //默认值
        Integer defaultNum = 0;
        //总数据量
        Integer total = MapUtils.getInteger(totalMap, "count", defaultNum);
        //每页显示数
        Integer pageSize = MapUtils.getInteger(parameters, "pageSize", defaultNum);
        //当前页
        Integer pageNumber = MapUtils.getInteger(parameters, "pageNumber", defaultNum);
        //构建Page对象
        Page page = Page.builder().setPageSize(pageSize).setPageNumber(pageNumber).setTotal(total).generate().create();
        parameters.put("offset", page.getOffset());
        parameters.put("pageSize", page.getPageSize());
        parameters.put("startRow", page.getStartRow());
        parameters.put("endRow", page.getEndRow());
        //分页查询
        logger.info(paginationSql + " " + ConvertUtils.map2Json(parameters));
        List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(paginationSql, parameters);
        page.setRows(rows);
        return page;
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
    private NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(Map<String, NamedParameterJdbcTemplate> namedParameterJdbcTemplateMap) {
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

    /**
     * @param namedParameterJdbcTemplates
     * @return
     */
    private NamedParameterJdbcTemplate getOrCreate(NamedParameterJdbcTemplate... namedParameterJdbcTemplates) {
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
}