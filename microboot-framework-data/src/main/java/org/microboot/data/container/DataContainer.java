package org.microboot.data.container;

import com.google.common.collect.Maps;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;

/**
 * @author 胡鹏
 */
public class DataContainer {

    public static final Map<String, Long> backoffTimeMap = Maps.newConcurrentMap();

    public static final Map<String, Long> backoffTimeLimitMap = Maps.newConcurrentMap();

    public static final Map<String, NamedParameterJdbcTemplate> initMap = Maps.newConcurrentMap();

    public static final Map<String, NamedParameterJdbcTemplate> slavesMap = Maps.newConcurrentMap();

    public static final Map<String, NamedParameterJdbcTemplate> othersMap = Maps.newConcurrentMap();
}
