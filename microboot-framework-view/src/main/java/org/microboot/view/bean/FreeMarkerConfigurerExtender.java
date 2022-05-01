package org.microboot.view.bean;

import com.jagregory.shiro.freemarker.ShiroTags;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.microboot.view.bean.tags.HasAllPermissionsTag;
import org.microboot.view.bean.tags.HasAnyPermissionsTag;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.IOException;

/**
 * @author 胡鹏
 */
public class FreeMarkerConfigurerExtender extends FreeMarkerConfigurer {

    @Override
    public void afterPropertiesSet() throws IOException, TemplateException {
        super.afterPropertiesSet();
        Configuration cfg = this.getConfiguration();
        // 添加shiro标签
        ShiroTags shiroTags = new ShiroTags();
        shiroTags.put("hasAllPermissions", new HasAllPermissionsTag());
        shiroTags.put("hasAnyPermissions", new HasAnyPermissionsTag());
        cfg.setSharedVariable("shiro", shiroTags);
    }
}
