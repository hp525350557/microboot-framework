package org.microboot.logging.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.constant.Constant;
import org.microboot.core.func.Func1;
import org.springframework.core.annotation.Order;

/**
 * @author 胡鹏
 */
@Aspect
@Order(0)
public class LoggingAspect {

    @Pointcut("@annotation(org.microboot.logging.annotation.Logging)")
    public void cut() {
    }

    @SuppressWarnings("unchecked")
    @Around("cut()")
    public Object aroundAspect(final ProceedingJoinPoint joinPoint) throws Exception {
        return ApplicationContextHolder.getBean(Constant.LOGGING_FUNC, Func1.class).func(joinPoint);
    }
}
