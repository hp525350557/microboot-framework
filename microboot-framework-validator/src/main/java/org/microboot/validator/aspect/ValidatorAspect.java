package org.microboot.validator.aspect;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.ValidatorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.constant.Constant;
import org.microboot.core.utils.ConvertUtils;
import org.microboot.core.utils.LoggerUtils;
import org.microboot.validator.annotation.Validator;
import org.microboot.validator.func.ValidatorFunc;
import org.microboot.validator.resolver.ValidatorResolver;
import org.microboot.web.utils.RequestUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author 胡鹏
 */
@Aspect
@Order(2)
public class ValidatorAspect {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final Environment environment;

    private final String prefix = "validators.";

    public ValidatorAspect(Environment environment) {
        this.environment = environment;
    }

    @Pointcut("@annotation(org.microboot.validator.annotation.Validator)")
    public void cut() {
    }

    @SuppressWarnings("unchecked")
    @Around("cut()")
    public Object aroundAspect(final ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Validator validatorAnnotation = AnnotationUtils.findAnnotation(method, Validator.class);
        String validatorName = validatorAnnotation.value();
        String validatorContent = ApplicationContextHolder.getBean(ValidatorResolver.class).getValidator(validatorName);
        if (StringUtils.isBlank(validatorContent)) {
            throw new ValidatorException("Validator error : The file of " + validatorName + " is null");
        }
        Map<String, Object> validatorContentMap;
        try {
            validatorContentMap = ConvertUtils.json2Map(validatorContent);
        } catch (Exception e) {
            LoggerUtils.error(logger, e);
            throw new ValidatorException("Validator error : The file of " + validatorName + " parsing error");
        }
        Map<String, Object> rulesMap = MapUtils.getMap(validatorContentMap, "rules");
        if (MapUtils.isEmpty(rulesMap)) {
            throw new ValidatorException("Validator error : The rules of " + validatorName + " is null");
        }
        for (String field : rulesMap.keySet()) {
            Map<String, Object> fieldMap = MapUtils.getMap(rulesMap, field);
            String defaultMessage = MapUtils.getString(fieldMap, "message");
            Map<String, Object> validators = MapUtils.getMap(fieldMap, "validators");
            if (MapUtils.isEmpty(validators)) {
                throw new ValidatorException("Validator error : The validators of " + field + " in " + validatorName + " is null");
            }
            String value = RequestUtils.getParameter(request, field);
            for (String validatorKey : validators.keySet()) {
                Map<String, Object> validator = MapUtils.getMap(validators, validatorKey);
                String validatorFuncClassName = environment.getProperty(prefix + validatorKey);
                if (StringUtils.isBlank(validatorFuncClassName)) {
                    throw new ValidatorException("Validator error : " + validatorKey + " does not match any classes");
                }
                Map<String, Object> result;
                try {
                    ValidatorFunc validatorFunc = ApplicationContextHolder.getBean(validatorFuncClassName, ValidatorFunc.class);
                    String message = StringUtils.isBlank(MapUtils.getString(validator, "message")) ? defaultMessage : MapUtils.getString(validator, "message");
                    if (StringUtils.isBlank(message)) {
                        message = field + " verification error";
                    }
                    result = validatorFunc.validate(validator, message, field, value, request);
                } catch (Exception e) {
                    throw new ValidatorException(e.getMessage());
                }
                if (result != null) {
                    String code = MapUtils.getString(result, "code");
                    if (StringUtils.equals(Constant.CODE_0, code)) {
                        throw new ValidatorException(MapUtils.getString(result, "msg"));
                    }
                }
            }
        }
        //调用执行目标方法，obj就是方法返回值
        return joinPoint.proceed(joinPoint.getArgs());
    }
}
