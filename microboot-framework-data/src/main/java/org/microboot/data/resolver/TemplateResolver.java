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
        /*
            据说当循环比较大时，可以将一些变量放在循环外面来提高性能
            · 很多习惯写法是将变量直接放在循环内，那么每次循环都会产生一个新的引用指向一个新的对象
              循环不结束，那么GC不能回收之前的对象
            · 将变量写在循环外面，那么每次循环，引用指向一个新的对象，之前的对象就没有变量指向它了，GC时，即使循环不结束也能回收掉
            对上述说法持保留意见，上课时学到的
         */
        for (int i = 0; i < resources.length; i++) {
            Resource resource = resources[i];
            String templateName = resource.getFilename();
            if (!StringUtils.endsWith(templateName, ".sql")) {
                continue;
            }
            try (InputStream in = resource.getInputStream();
                 StringWriter writer = new StringWriter()) {
                IOUtils.copy(in, writer, StandardCharsets.UTF_8.name());
                String templateKey = StringUtils.replace(templateName, ".sql", "");
                String templateContent = writer.toString();
                StringTemplateLoader templateLoader = new StringTemplateLoader();
                templateLoader.putTemplate(templateKey, templateContent);
                templateLoaderArray[i] = templateLoader;
            }
        }
        MultiTemplateLoader multiTemplateLoader = new MultiTemplateLoader(templateLoaderArray);
        this.configuration.setTemplateLoader(multiTemplateLoader);
    }

    private String getSql(Template template, Map<String, ?> parameters) throws Exception {
        if (template == null) {
            return null;
        }
        try (StringWriter writer = new StringWriter()) {
            template.process(parameters, writer);
            String sql = writer.toString();
            return sql;
        }
    }
}
