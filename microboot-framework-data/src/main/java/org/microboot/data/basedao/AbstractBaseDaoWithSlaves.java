package org.microboot.data.basedao;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.constant.Constant;
import org.microboot.core.utils.ConvertUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * @author 胡鹏
 */
public abstract class AbstractBaseDaoWithSlaves extends AbstractBaseDaoWithOthers {

    /**
     * @param templateName
     * @param parameters
     * @return
     * @throws Exception
     */
    public int executeWithSlaves(String templateName, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.SLAVES_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.execute(templateName, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param javaBean
     * @return
     * @throws Exception
     */
    public int executeWithSlaves(String templateName, Object javaBean) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.executeWithSlaves(templateName, parameters);
    }

    /**
     * @param templateName
     * @param paramKey
     * @param paramValue
     * @return
     * @throws Exception
     */
    public int executeWithSlaves(String templateName, String paramKey, Object paramValue) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.executeWithSlaves(templateName, parameters);
    }

    /**
     * @param templateName
     * @param parameterSource
     * @return
     * @throws Exception
     */
    public int executeWithSlaves(String templateName, MapSqlParameterSource parameterSource) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.SLAVES_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.execute(templateName, parameterSource, namedParameterJdbcTemplate);
    }

    /**
     * @param templateName
     * @param parametersList
     * @return
     * @throws Exception
     */
    public int[] executeBatchWithSlaves(String templateName, List<?> parametersList) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.SLAVES_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.executeBatch(templateName, parametersList, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param parameters
     * @return
     * @throws Exception
     */
    public int executeBySqlWithSlaves(String sql, Map<String, ?> parameters) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.SLAVES_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.executeBySql(sql, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param javaBean
     * @return
     * @throws Exception
     */
    public int executeBySqlWithSlaves(String sql, Object javaBean) throws Exception {
        /*
         * 实体传参还可以用BeanPropertySqlParameterSource来封装javaBean
         */
        Map<String, Object> parameters = ConvertUtils.bean2Map(javaBean);
        return this.executeBySqlWithSlaves(sql, parameters);
    }

    /**
     * @param sql
     * @param paramKey
     * @param paramValue
     * @return
     * @throws Exception
     */
    public int executeBySqlWithSlaves(String sql, String paramKey, Object paramValue) throws Exception {
        if (StringUtils.isBlank(paramKey)) {
            throw new IllegalArgumentException("paramKey must not be null");
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(paramKey, paramValue);
        return this.executeBySqlWithSlaves(sql, parameters);
    }

    /**
     * @param sql
     * @param parameterSource
     * @return
     * @throws Exception
     */
    public int executeBySqlWithSlaves(String sql, MapSqlParameterSource parameterSource) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.SLAVES_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.executeBySql(sql, parameterSource, namedParameterJdbcTemplate);
    }

    /**
     * @param sql
     * @param parametersList
     * @return
     * @throws Exception
     */
    public int[] executeBatchBySqlWithSlaves(String sql, List<?> parametersList) throws Exception {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ApplicationContextHolder.getBean(Constant.SLAVES_JDBC_TEMPLATE, NamedParameterJdbcTemplate.class);
        return this.executeBatchBySql(sql, parametersList, namedParameterJdbcTemplate);
    }
}