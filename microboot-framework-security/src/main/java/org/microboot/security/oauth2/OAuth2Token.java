package org.microboot.security.oauth2;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author 胡鹏
 */
public class OAuth2Token implements AuthenticationToken {

    private static final long serialVersionUID = 1L;

    private final String principal;

    private final String credentials;

    public OAuth2Token(String principal, String credentials) {
        this.principal = principal;
        this.credentials = credentials;
    }

    @Override
    public String getPrincipal() {
        return principal;
    }

    @Override
    public String getCredentials() {
        return credentials;
    }
}
