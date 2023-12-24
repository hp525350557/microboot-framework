package org.microboot.core.constant;

/**
 * @author 胡鹏
 */
public class Constant {

    ////////////////////////////////错误提示////////////////////////////////
    public static final String ERROR_1 = "请求失败，系统错误，请联系管理员处理";
    public static final String ERROR_2 = "请求失败，认证错误，请正确登录系统";
    public static final String ERROR_3 = "请求失败，授权错误，请联系管理员授权";
    public static final String ERROR_4 = "请求失败，校验错误，请检查数据是否正确";
    public static final String ERROR_5 = "请求失败，请求方式错误";

    ////////////////////////////////统一key////////////////////////////////
    public static final String APPLICATION_CONTEXT_HOLDER = "org.microboot.core.bean.ApplicationContextHolder";
    public static final String CACHE_HOLDER = "org.microboot.core.bean.CacheHolder";

    public static final String MASTER_DATA_SOURCE = "MASTER_DATA_SOURCE";
    public static final String MASTER_JDBC_TEMPLATE = "MASTER_JDBC_TEMPLATE";
    public static final String SLAVES_JDBC_TEMPLATE = "SLAVES_JDBC_TEMPLATE";
    public static final String OTHERS_JDBC_TEMPLATE = "OTHERS_JDBC_TEMPLATE";
    public static final String FREEMARKER_CONFIGURATION = "freemarker.template.Configuration";

    public static final String SECURITY_USERNAME = "SECURITY-USERNAME";
    public static final String SECURITY_TOKEN = "SECURITY-TOKEN";
    public static final String SECURITY_CAPTCHA_IMAGE = "SECURITY-CAPTCHA-IMAGE";
    public static final String SECURITY_CAPTCHA = "SECURITY-CAPTCHA";
    public static final String SECURITY_GET_AUTHORIZATION_INFO_FUNC = "SECURITY_GET_AUTHORIZATION_INFO_FUNC";
    public static final String SECURITY_VALIDATE_AUTHENTICATION_INFO_FUNC = "SECURITY_VALIDATE_AUTHENTICATION_INFO_FUNC";
    public static final String LOGGING_FUNC = "LOGGING_FUNC";

    ////////////////////////////////缓存key表达式////////////////////////////////
    public static final String CACHE_NAME = "microboot-cache";
    public static final String CACHE_LOCAL_NAME = "microboot-local-cache";
    public static final String CACHE_CENTRAL_NAME = "microboot-central-cache";
    public static final String CACHE_ROOT_TARGET_CLASSNAME = "#root.target.getClassName() + '&' + ";
    public static final String CACHE_ROOT_TARGET_CLASSNAME_METHOD = "#root.target.getClassName() + '&' + #root.method.name + '&' + ";
    public static final String CACHE_ROOT_TARGETCLASS_NAME = "#root.targetClass.name + '&' + ";
    public static final String CACHE_ROOT_TARGETCLASS_NAME_METHOD = "#root.targetClass.name + '&' + #root.method.name + '&' + ";
    public static final String CACHE_ROOT_METHOD_NAME = "#root.method.name + '&' + ";

    ////////////////////////////////通用code////////////////////////////////
    public static final String CODE_0 = "0";
    public static final String CODE_1 = "1";
    public static final String CODE_2 = "2";
    public static final String CODE_3 = "3";
    public static final String CODE_4 = "4";
    public static final String CODE_5 = "5";
    public static final String CODE_6 = "6";
    public static final String CODE_7 = "7";
    public static final String CODE_8 = "8";
    public static final String CODE_9 = "9";
}
