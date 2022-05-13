package org.microboot.core.entity;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.microboot.core.utils.LoggerUtils;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 胡鹏
 */
@Setter
@Getter
public class Token implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(Token.class);

    //主题
    private String subject;
    //到期时间（用于计算失效时间）
    private long expire;
    //失效时间
    private long expireTime;
    //扩展参数
    private Map<String, Object> expand;

    static {
        try {
            /*
                需求：
                    使用Builder构建对象，并且禁用new关键字创建对象
                问题：
                    大多数时候，利用反射(类.class)创建该对象时，默认调用的是无参构造方法
                    所以，必须声明一个无参构造方法，但是又不想将无参构造方法的访问级别设置为public
                解决：
                    通过ReflectionUtils.makeAccessible(Constructor<?> ctor)
                    设置在反射时忽略无参构造方法的private修饰符
             */
            ReflectionUtils.makeAccessible(Token.class.getDeclaredConstructor());
        } catch (Exception e) {
            LoggerUtils.error(logger, e);
        }
    }

    private Token() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean verifySubject(String subject) {
        if (StringUtils.isBlank(this.subject) || StringUtils.isBlank(subject)) {
            return false;
        }
        return StringUtils.equalsIgnoreCase(this.subject, subject);
    }

    public boolean verifyExpireTime() {
        if (this.expireTime <= 0) {
            return false;
        }
        return System.currentTimeMillis() <= this.expireTime;
    }

    public static final class Builder {

        //Token对象
        private final Token token = new Token();

        private Builder() {
        }

        public Builder setSubject(String subject) {
            this.token.subject = subject;
            return this;
        }

        public Builder setExpire(long expire) {
            this.token.expire = NumberUtils.max(-1, expire);
            return this;
        }

        public Builder setExpireTime(long expireTime) {
            this.token.expireTime = expireTime;
            return this;
        }

        public Builder setExpand(Map<String, Object> expand) {
            this.token.expand = expand;
            return this;
        }

        /**
         * 以当前时间作为开始时间，计算绝对失效时间
         *
         * @return
         */
        public Builder generate() {
            return this.generate(System.currentTimeMillis());
        }

        /**
         * 以传入时间作为开始时间，计算相对失效时间
         *
         * @param startTimeMillis
         * @return
         */
        public Builder generate(long startTimeMillis) {
            if (this.token.expire == -1) {
                this.token.expireTime = Long.MAX_VALUE;
            } else {
                this.token.expireTime = startTimeMillis + this.token.expire;
            }
            return this;
        }

        public Token create() {
            return this.token;
        }
    }
}
