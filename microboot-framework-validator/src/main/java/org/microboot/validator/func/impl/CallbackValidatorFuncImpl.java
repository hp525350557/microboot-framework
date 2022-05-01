package org.microboot.validator.func.impl;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.ValidatorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.utils.LoggerUtils;
import org.microboot.core.utils.ResultUtils;
import org.microboot.validator.func.ValidatorFunc;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 胡鹏
 */
public class CallbackValidatorFuncImpl implements ValidatorFunc {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> validate(Map<String, Object> validatorMap, String message, String field, String value,
                                        HttpServletRequest request) throws Exception {
        String callback = MapUtils.getString(validatorMap, "callback");
        if (StringUtils.isBlank(callback)) {
            LoggerUtils.error(logger, new ValidatorException("Validator error : callback is not defined."));
            return ResultUtils.error();
        }
        Map<String, Object> result;
        try {
            Class<ValidatorFunc> callbackClass = (Class<ValidatorFunc>) Class.forName(callback);
            ValidatorFunc validatorFunc = ApplicationContextHolder.getBean(callbackClass);
            result = validatorFunc.validate(validatorMap, message, field, value, request);
        } catch (Exception e) {
            LoggerUtils.error(logger, e);
            return ResultUtils.error();
        }
        return result;
    }

}
