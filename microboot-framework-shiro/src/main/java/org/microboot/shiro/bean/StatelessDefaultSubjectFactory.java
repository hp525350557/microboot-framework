package org.microboot.shiro.bean;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;

/**
 * @author 胡鹏
 *
 * 无状态认证需要禁用Session
 * 参考：https://blog.51cto.com/u_11142439/3085621
 */
public class StatelessDefaultSubjectFactory extends DefaultWebSubjectFactory {

    private boolean sessionCreationEnable;

    public StatelessDefaultSubjectFactory(boolean sessionCreationEnabled) {
        this.sessionCreationEnable = sessionCreationEnabled;
    }

    @Override
    public Subject createSubject(SubjectContext context) {
        //禁止创建session
        context.setSessionCreationEnabled(this.sessionCreationEnable);
        return super.createSubject(context);
    }
}
