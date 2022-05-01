package org.microboot.validator.func.impl;

import org.apache.commons.lang3.StringUtils;
import org.microboot.core.utils.ResultUtils;
import org.microboot.validator.func.ValidatorFunc;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 胡鹏
 */
public class NotEmptyValidatorFuncImpl implements ValidatorFunc {

    @Override
    public Map<String, Object> validate(Map<String, Object> validatorMap, String message, String field, String value,
                                        HttpServletRequest request) throws Exception {
        if (StringUtils.isNotBlank(value)) {
            return null;
        }
        return ResultUtils.error(message);
    }

}
