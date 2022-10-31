package org.microboot.validator.resolver;

import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.microboot.core.bean.ApplicationContextHolder;
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

    private static final Map<String, String> validatorMap = Maps.newHashMap();

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
            之所以将变量放在循环外面，是为了提高性能
            · 很多习惯写法是将变量直接放在循环内，那么每次循环都会产生一个新的引用指向一个新的对象
              循环不结束，那么GC不能回收之前的对象
            · 将变量写在循环外面，那么每次循环，引用指向一个新的对象，之前的对象就没有变量指向它了，GC时，即使循环不结束也能回收掉
         */
        InputStream in;
        StringWriter writer;
        String validatorName;
        String validatorContent;
        for (Resource resource : resources) {
            validatorName = resource.getFilename();
            if (!StringUtils.endsWith(validatorName, ".json")) {
                continue;
            }
            in = resource.getInputStream();
            writer = new StringWriter();
            IOUtils.copy(in, writer, StandardCharsets.UTF_8.name());
            validatorContent = RegExUtils.replacePattern(writer.toString(), "\\r|\\n|\\t", "");
            in.close();
            writer.close();
            if (StringUtils.isBlank(StringUtils.trim(validatorContent))) {
                throw new Exception("File read error : File can not be null");
            }
            validatorMap.put(validatorName, validatorContent);
        }
    }

    public String getValidator(String validatorName) {
        return validatorMap.get(validatorName);
    }
}
