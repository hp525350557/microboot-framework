package org.microboot.core.func;

/**
 * @author 胡鹏
 */
public interface FuncN<T, U> {
    @SuppressWarnings("unchecked")
	T func(U... u) throws Exception;
}
