package org.microboot.data.aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.data.basedao.BaseDao;
import org.springframework.core.annotation.Order;

/**
 * @author 胡鹏
 */
@Aspect
@Order(0)
public class ClearThreadLocalAspect {

    /**
     * @within对象级别
     * @annotation方法级别
     */
    @Pointcut("@within(org.microboot.data.annotation.ClearThreadLocal) || @annotation(org.microboot.data.annotation.ClearThreadLocal)")
    public void cut() {
    }

    @After("cut()")
    public void after() {
        ApplicationContextHolder.getBean(BaseDao.class).remove();
    }
}
