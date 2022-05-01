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
        for (Resource resource : resources) {
            String validatorName = resource.getFilename();
            if (!StringUtils.endsWith(validatorName, ".json")) {
                continue;
            }
            InputStream in = resource.getInputStream();
            StringWriter writer = new StringWriter();
            IOUtils.copy(in, writer, StandardCharsets.UTF_8.name());
            String validatorContent = RegExUtils.replacePattern(writer.toString(), "\\r|\\n|\\t", "");
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
