package com.aurora.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.function.Supplier;

@Component
public class DynamicAuthorizationManagerImpl implements AuthorizationManager<RequestAuthorizationContext> {

    @Autowired
    private FilterInvocationSecurityMetadataSourceImpl securityMetadataSource;

    @Autowired
    private AccessDecisionManager accessDecisionManager;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier,
                                       RequestAuthorizationContext context) {
        HttpServletRequest request = context.getRequest();
        Collection<ConfigAttribute> attributes = securityMetadataSource.getAttributes(request);
        if (attributes == null || attributes.isEmpty()) {
            return new AuthorizationDecision(true);
        }
        Authentication authentication = authenticationSupplier.get();
        accessDecisionManager.decide(authentication, null, attributes);
        return new AuthorizationDecision(true);
    }
}
