package org.microboot.view.bean.tags;

import com.jagregory.shiro.freemarker.PermissionTag;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 胡鹏
 */
public class HasAllPermissionsTag extends PermissionTag {

    @Override
    protected boolean showTagBody(String permissions) {
        if (StringUtils.isBlank(permissions)) {
            return false;
        }
        String[] permissionArray = StringUtils.split(permissions, ",");
        for (String permission : permissionArray) {
            if (!this.isPermitted(permission)) {
                return false;
            }
        }
        return true;
    }
}
