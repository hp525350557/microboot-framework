package org.microboot.core.func;

/**
 * @author 胡鹏
 */
public interface Func2<T, U1, U2> {
    T func(U1 u1, U2 u2) throws Exception;
}
