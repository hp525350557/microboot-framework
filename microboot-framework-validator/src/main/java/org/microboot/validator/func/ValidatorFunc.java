package org.microboot.validator.func;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 胡鹏
 */
public interface ValidatorFunc {

    Map<String, Object> validate(Map<String, Object> validatorMap, String message, String field, String value, HttpServletRequest request) throws Exception;
}
