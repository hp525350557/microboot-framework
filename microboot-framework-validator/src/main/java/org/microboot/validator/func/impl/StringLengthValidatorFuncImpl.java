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
public class StringLengthValidatorFuncImpl implements ValidatorFunc {

    @Override
    public Map<String, Object> validate(Map<String, Object> validatorMap, String message, String field, String value,
                                        HttpServletRequest request) throws Exception {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        if (validatorMap.containsKey("min")) {
            int min = MapUtils.getIntValue(validatorMap, "min");
            if (!GenericValidator.minLength(value, min)) {
                return ResultUtils.error(message);
            }
        }

        if (validatorMap.containsKey("max")) {
            int max = MapUtils.getIntValue(validatorMap, "max");
            if (!GenericValidator.maxLength(value, max)) {
                return ResultUtils.error(message);
            }
        }

        return null;
    }

}
