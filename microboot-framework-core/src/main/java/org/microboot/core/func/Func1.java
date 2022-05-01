package org.microboot.core.func;

/**
 * @author 胡鹏
 */
public interface Func1<T, U> {
    T func(U u) throws Exception;
}
