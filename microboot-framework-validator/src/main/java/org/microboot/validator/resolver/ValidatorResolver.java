package org.microboot.validator.resolver;

import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.utils.ConvertUtils;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author 胡鹏
 */
public class ValidatorResolver {

    private final Map<String, Map<String, Object>> validatorMap = Maps.newHashMap();

    @PostConstruct
    public void init() throws Exception {
        Resource[] resources = ApplicationContextHolder.getApplicationContext().getResources("classpath*:/validators/**/*.json");
        this.addJsons(resources);
    }

    public void addJsons(Resource[] resources) throws Exception {
        if (resources == null || resources.length == 0) {
            return;
        }
        /*
            据说当循环比较大时，可以将一些变量放在循环外面来提高性能
            · 很多习惯写法是将变量直接放在循环内，那么每次循环都会产生一个新的引用指向一个新的对象
              循环不结束，那么GC不能回收之前的对象
            · 将变量写在循环外面，那么每次循环，引用指向一个新的对象，之前的对象就没有变量指向它了，GC时，即使循环不结束也能回收掉
            对上述说法持保留意见，上课时学到的
         */
        for (Resource resource : resources) {
            String validatorName = resource.getFilename();
            if (!StringUtils.endsWith(validatorName, ".json")) {
                continue;
            }
            try (InputStream in = resource.getInputStream();
                 StringWriter writer = new StringWriter()) {
                IOUtils.copy(in, writer, StandardCharsets.UTF_8.name());
                //获取校验文件内容
                String validatorContent = RegExUtils.replacePattern(writer.toString(), "\\r|\\n|\\t", "");
                if (StringUtils.isBlank(StringUtils.trim(validatorContent))) {
                    throw new IllegalArgumentException("Validator error : The file of " + validatorName + " is null");
                }
                //解析校验文件内容
                Map<String, Object> validatorContentMap = ConvertUtils.json2Map(validatorContent);
                //获取校验规则
                Map<String, Object> rulesMap = MapUtils.getMap(validatorContentMap, "rules");
                if (MapUtils.isEmpty(rulesMap)) {
                    throw new IllegalArgumentException("Validator error : The rules of " + validatorName + " is null");
                }
                //绑定校验规则
                validatorMap.put(validatorName, rulesMap);
            }
        }
    }

    public Map<String, Object> getValidator(String validatorName) {
        return validatorMap.get(validatorName);
    }
}
