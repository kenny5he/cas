package com.microfoolish.it.login.cas.authentication.adaptors;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import org.apereo.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.configuration.model.support.jdbc.authn.QueryJdbcAuthenticationProperties;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.monitor.Monitorable;

@Slf4j
@Monitorable
public class MailQueryAuthenticationHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler<QueryJdbcAuthenticationProperties> {
    protected MailQueryAuthenticationHandler(QueryJdbcAuthenticationProperties properties, ServicesManager servicesManager, PrincipalFactory principalFactory, DataSource dataSource) {
        super(properties, servicesManager, principalFactory, dataSource);
    }

    @Override
    protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(UsernamePasswordCredential credential, String originalPassword) throws Throwable {
        return null;
    }

    @Override
    public boolean preAuthenticate(Credential credential) {
        return super.preAuthenticate(credential);
    }

    @Override
    public AuthenticationHandlerExecutionResult postAuthenticate(Credential credential, AuthenticationHandlerExecutionResult result) {
        return super.postAuthenticate(credential, result);
    }
}
