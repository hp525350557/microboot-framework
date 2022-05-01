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
        Integer min = MapUtils.getDouble(validatorMap, "min") != null ? MapUtils.getDouble(validatorMap, "min").intValue() : null;
        Integer max = MapUtils.getDouble(validatorMap, "max") != null ? MapUtils.getDouble(validatorMap, "max").intValue() : null;
        if (min != null) {
            if (!GenericValidator.minLength(value, min)) {
                return ResultUtils.error(message);
            }
        }
        if (max != null) {
            if (!GenericValidator.maxLength(value, max)) {
                return ResultUtils.error(message);
            }
        }
        return null;
    }

}
