package org.microboot.shiro.oauth2;

import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.constant.Constant;
import org.microboot.core.func.Func1;
import org.microboot.core.func.Func2;

import java.util.Set;

/**
 * @author 胡鹏
 */
public class OAuth2Realm extends AuthorizingRealm {

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;
    }

    /**
     * 授权
     */
    @SuppressWarnings("unchecked")
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String principal = principals.getPrimaryPrincipal().toString();
        Set<String> roleSet = null;
        Set<String> permissionSet = null;
        try {
            boolean notMissing = ApplicationContextHolder.getApplicationContext().containsLocalBean(Constant.SECURITY_GET_AUTHORIZATION_INFO_ROLE_FUNC);
            if (notMissing) {
                roleSet = (Set<String>) ApplicationContextHolder.getBean(Constant.SECURITY_GET_AUTHORIZATION_INFO_ROLE_FUNC, Func1.class).func(principal);
            }
        } catch (Exception e) {
            throw new AuthorizationException(Constant.ERROR_1);
        }
        try {
            boolean notMissing = ApplicationContextHolder.getApplicationContext().containsLocalBean(Constant.SECURITY_GET_AUTHORIZATION_INFO_PERMISSION_FUNC);
            if (notMissing) {
                permissionSet = (Set<String>) ApplicationContextHolder.getBean(Constant.SECURITY_GET_AUTHORIZATION_INFO_PERMISSION_FUNC, Func1.class).func(principal);
            }
        } catch (Exception e) {
            throw new AuthorizationException(Constant.ERROR_1);
        }
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        if (CollectionUtils.isNotEmpty(roleSet)) {
            simpleAuthorizationInfo.setRoles(roleSet);
        }
        if (CollectionUtils.isNotEmpty(permissionSet)) {
            simpleAuthorizationInfo.setStringPermissions(permissionSet);
        }
        return simpleAuthorizationInfo;
    }

    /**
     * 认证
     */
    @SuppressWarnings("unchecked")
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        try {
            OAuth2Token oauth2Token = (OAuth2Token) authenticationToken;
            String principal = oauth2Token.getPrincipal();
            String credentials = oauth2Token.getCredentials();
            AuthenticationException ex = (AuthenticationException) ApplicationContextHolder.getBean(Constant.SECURITY_VALIDATE_AUTHENTICATION_INFO_FUNC, Func2.class).func(principal, credentials);
            if (ex != null) {
                throw ex;
            }
            return new SimpleAuthenticationInfo(principal, credentials, getName());
        } catch (Exception e) {
            throw new AuthenticationException(e);
        }
    }
}
