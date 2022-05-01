package org.microboot.data.resolver;

import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.constant.Constant;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author 胡鹏
 */
public class TemplateResolver {

    private final Configuration configuration = ApplicationContextHolder.getBean(Constant.FREEMARKER_CONFIGURATION, Configuration.class);

    @PostConstruct
    public void init() throws Exception {
        Resource[] resources = ApplicationContextHolder.getApplicationContext().getResources("classpath*:/sql/**/*.sql");
        this.putTemplates(resources);
    }

    public String processTemplate(String templateName, Map<String, ?> parameters) throws Exception {
        Template template = this.configuration.getTemplate(templateName);
        return getSql(template, parameters);
    }

    private void putTemplates(Resource[] resources) throws Exception {
        if (resources == null || resources.length == 0) {
            return;
        }
        TemplateLoader[] templateLoaderArray = new TemplateLoader[resources.length];
        for (int i = 0; i < resources.length; i++) {
            Resource resource = resources[i];
            String templateName = resource.getFilename();
            if (!StringUtils.endsWith(templateName, ".sql")) {
                continue;
            }
            InputStream in = resource.getInputStream();
            StringWriter writer = new StringWriter();
            IOUtils.copy(in, writer, StandardCharsets.UTF_8.name());
            String templateKey = StringUtils.replace(templateName, ".sql", "");
            String templateContent = writer.toString();
            in.close();
            writer.close();
            StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
            stringTemplateLoader.putTemplate(templateKey, templateContent);
            templateLoaderArray[i] = stringTemplateLoader;
        }
        MultiTemplateLoader multiTemplateLoader = new MultiTemplateLoader(templateLoaderArray);
        this.configuration.setTemplateLoader(multiTemplateLoader);
    }

    private String getSql(Template template, Map<String, ?> parameters) throws Exception {
        if (template == null) {
            return null;
        }
        StringWriter writer = new StringWriter();
        template.process(parameters, writer);
        String sql = writer.toString();
        writer.close();
        return sql;
    }
}
