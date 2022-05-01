package org.microboot.validator.func.impl;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.microboot.core.utils.ResultUtils;
import org.microboot.validator.func.ValidatorFunc;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 胡鹏
 */
public class RegexpValidatorFuncImpl implements ValidatorFunc {

    @Override
    public Map<String, Object> validate(Map<String, Object> validatorMap, String message, String field, String value,
                                        HttpServletRequest request) throws Exception {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        String regexp = MapUtils.getString(validatorMap, "regexp");
        if (GenericValidator.matchRegexp(value, regexp)) {
            return null;
        }
        return ResultUtils.error(message);
    }

}
