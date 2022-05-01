package org.microboot.security.oauth2;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.microboot.core.constant.Constant;
import org.microboot.web.utils.RequestUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 胡鹏
 */
public class OAuth2Filter extends AuthenticatingFilter {

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String principal = RequestUtils.getParameter(httpRequest, Constant.SECURITY_USERNAME);
        String credentials = RequestUtils.getParameter(httpRequest, Constant.SECURITY_TOKEN);
        return new OAuth2Token(principal, credentials);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        if (!subject.isAuthenticated()) {
            return executeLogin(request, response);
        }
        return true;
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException ex, ServletRequest req, ServletResponse resp) {
        throw ex;
    }
}
