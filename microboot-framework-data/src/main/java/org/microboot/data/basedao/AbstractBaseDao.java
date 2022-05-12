package org.microboot.data.basedao;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.entity.Page;
import org.microboot.core.utils.ConvertUtils;
import org.microboot.core.utils.LoggerUtils;
import org.microboot.data.resolver.TemplateResolver;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author 胡鹏
 */
public abstract class AbstractBaseDao extends TransmittableThreadLocal<NamedParameterJdbcTemplate> {

    private final Logger logger = LogManager.getLogger(this.getClass());

    /**
     * @param templateName
     * @param parameters
     * @param namedParameterJdbcTemplate
     * @return
     * @throws Exception
     */
    protected List<Map<String, Object>> queryForList(String templateName, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
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
    protected <T> List<T> queryForList(String templateName, Map<String, ?> parameters, Class<T> clazz, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
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
    protected Map<String, Object> queryForMap(String templateName, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
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
    protected <T> T queryForObject(String templateName, Map<String, ?> parameters, Class<T> clazz, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
        String sql = ApplicationContextHolder.getBean(TemplateResolver.class).processTemplate(templateName, parameters);
        return this.queryForObjectBySql(sql, parameters, clazz, namedParameterJdbcTemplate);
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
    protected <T> T query(String templateName, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate, ResultSetExtractor<T> rse) throws Exception {
        String sql = ApplicationContextHolder.getBean(TemplateResolver.class).processTemplate(templateName, parameters);
        return this.queryBySql(sql, parameters, namedParameterJdbcTemplate, rse);
    }

    /**
     * @param templateName
     * @param parameters
     * @param namedParameterJdbcTemplate
     * @return
     * @throws Exception
     */
    protected SqlRowSet queryForSqlRowSet(String templateName, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
        String sql = ApplicationContextHolder.getBean(TemplateResolver.class).processTemplate(templateName, parameters);
        return this.queryForSqlRowSetBySql(sql, parameters, namedParameterJdbcTemplate);
    }

    /**
     * @param paginationCount
     * @param pagination
     * @param parameters
     * @param namedParameterJdbcTemplate
     * @return
     * @throws Exception
     */
    protected Page pagination(String paginationCount, String pagination, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
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
    protected List<Map<String, Object>> queryForListBySql(String sql, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
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
    protected <T> List<T> queryForListBySql(String sql, Map<String, ?> parameters, Class<T> clazz, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
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
    protected Map<String, Object> queryForMapBySql(String sql, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
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
    protected <T> T queryForObjectBySql(String sql, Map<String, ?> parameters, Class<T> clazz, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
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
     * @param sql
     * @param parameters
     * @param namedParameterJdbcTemplate
     * @param rse
     * @param <T>
     * @return
     * @throws Exception
     */
    protected <T> T queryBySql(String sql, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate, ResultSetExtractor<T> rse) throws Exception {
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
     * @param sql
     * @param parameters
     * @param namedParameterJdbcTemplate
     * @return
     * @throws Exception
     */
    protected SqlRowSet queryForSqlRowSetBySql(String sql, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
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
     * @param paginationCountSql
     * @param paginationSql
     * @param parameters
     * @param namedParameterJdbcTemplate
     * @return
     * @throws Exception
     */
    protected Page paginationBySql(String paginationCountSql, String paginationSql, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
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
    protected int execute(String templateName, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
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
    protected int[] executeBatch(String templateName, List<?> parametersList, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
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
    protected int executeBySql(String sql, Map<String, ?> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        logger.info(sql + " -> " + ConvertUtils.map2Json(parameters));
        this.getOrCreate(namedParameterJdbcTemplate);
        return namedParameterJdbcTemplate.update(sql, parameters);
    }

    /**
     * @param templateName
     * @param parameterSource
     * @param namedParameterJdbcTemplate
     * @return
     * @throws Exception
     */
    protected int execute(String templateName, MapSqlParameterSource parameterSource, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
        Assert.notNull(parameterSource, "MapSqlParameterSource must not be null");
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
    protected int executeBySql(String sql, MapSqlParameterSource parameterSource, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws Exception {
        logger.info(sql + " -> " + ConvertUtils.object2Json(parameterSource));
        this.getOrCreate(namedParameterJdbcTemplate);
        return namedParameterJdbcTemplate.update(sql, parameterSource);
    }

    /**
     * @param sql
     * @param parametersList
     * @param namedParameterJdbcTemplate
     * @return
     */
    protected int[] executeBatchBySql(String sql, List<?> parametersList, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
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
    protected Page getPage(String paginationCountSql, String paginationSql, Map<String, Object> parameters, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
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