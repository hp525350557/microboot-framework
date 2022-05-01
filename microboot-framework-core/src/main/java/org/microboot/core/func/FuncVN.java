package org.microboot.core.func;

/**
 * @author 胡鹏
 */
public interface FuncVN<U> {
    @SuppressWarnings("unchecked")
	void func(U... u) throws Exception;
}
