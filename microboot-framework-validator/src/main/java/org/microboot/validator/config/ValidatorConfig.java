package org.microboot.validator.config;

import org.microboot.core.constant.Constant;
import org.microboot.validator.aspect.ValidatorAspect;
import org.microboot.validator.func.impl.*;
import org.microboot.validator.resolver.ValidatorResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

/**
 * @author 胡鹏
 */
@Configuration
@DependsOn(Constant.APPLICATION_CONTEXT_HOLDER)
public class ValidatorConfig {

    /**
     * 初始化ValidatorAspect
     *
     * @param environment
     * @return
     */
    @Bean(name = "org.microboot.validator.aspect.ValidatorAspect")
    public ValidatorAspect initValidatorAspect(Environment environment) {
        return new ValidatorAspect(environment);
    }

    /**
     * 初始化CallbackValidatorFuncImpl
     *
     * @return
     */
    @Bean(name = "org.microboot.validator.func.impl.CallbackValidatorFuncImpl")
    public CallbackValidatorFuncImpl initCallbackValidatorFuncImpl() {
        return new CallbackValidatorFuncImpl();
    }

    /**
     * 初始化DateValidatorFuncImpl
     *
     * @return
     */
    @Bean(name = "org.microboot.validator.func.impl.DateValidatorFuncImpl")
    public DateValidatorFuncImpl initDateValidatorFuncImpl() {
        return new DateValidatorFuncImpl();
    }

    /**
     * 初始化EmailValidatorFuncImpl
     *
     * @return
     */
    @Bean(name = "org.microboot.validator.func.impl.EmailValidatorFuncImpl")
    public EmailValidatorFuncImpl initEmailValidatorFuncImpl() {
        return new EmailValidatorFuncImpl();
    }

    /**
     * 初始化NotEmptyValidatorFuncImpl
     *
     * @return
     */
    @Bean(value = "org.microboot.validator.func.impl.NotEmptyValidatorFuncImpl")
    public NotEmptyValidatorFuncImpl initNotEmptyValidatorFuncImpl() {
        return new NotEmptyValidatorFuncImpl();
    }

    /**
     * 初始化RegexpValidatorFuncImpl
     *
     * @return
     */
    @Bean(name = "org.microboot.validator.func.impl.RegexpValidatorFuncImpl")
    public RegexpValidatorFuncImpl initRegexpValidatorFuncImpl() {
        return new RegexpValidatorFuncImpl();
    }

    /**
     * 初始化StringLengthValidatorFuncImpl
     *
     * @return
     */
    @Bean(name = "org.microboot.validator.func.impl.StringLengthValidatorFuncImpl")
    public StringLengthValidatorFuncImpl initStringLengthValidatorFuncImpl() {
        return new StringLengthValidatorFuncImpl();
    }

    /**
     * 初始化ValidatorResolver
     *
     * @return
     */
    @Bean(name = "org.microboot.validator.resolver.ValidatorResolver")
    public ValidatorResolver initValidatorResolver() {
        return new ValidatorResolver();
    }

}
