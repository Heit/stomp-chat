package ru.kosinov.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import ru.kosinov.entity.User;

import java.util.Collection;

public class JWSAuthenticationToken extends AbstractAuthenticationToken implements Authentication {

    private static final long serialVersionUID = 1L;

    private String token;
    private User principal;

    public JWSAuthenticationToken(String token) {
        this(token, null, null);
    }

    public JWSAuthenticationToken(String token, User principal, Collection<GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        this.principal = principal;
    }

    public Object getCredentials() {
        return token;
    }

    public Object getPrincipal() {
        return principal;
    }

}
