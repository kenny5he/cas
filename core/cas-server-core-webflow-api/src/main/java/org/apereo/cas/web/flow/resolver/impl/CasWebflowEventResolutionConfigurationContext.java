package org.apereo.cas.web.flow.resolver.impl;

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.audit.AuditableExecution;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationServiceSelectionPlan;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.MultifactorAuthenticationContextValidator;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.ticket.registry.TicketRegistrySupport;
import org.apereo.cas.web.cookie.CasCookieBuilder;
import org.apereo.cas.web.flow.SingleSignOnParticipationStrategy;
import org.apereo.cas.web.support.ArgumentExtractor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

/**
 * This is {@link CasWebflowEventResolutionConfigurationContext}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
@ToString
@Getter
@Setter
@SuperBuilder
public class CasWebflowEventResolutionConfigurationContext {
    private final AuthenticationSystemSupport authenticationSystemSupport;

    private final CentralAuthenticationService centralAuthenticationService;

    private final ServicesManager servicesManager;

    private final TicketRegistrySupport ticketRegistrySupport;

    private final TicketRegistry ticketRegistry;

    private final CasCookieBuilder warnCookieGenerator;

    private final AuthenticationServiceSelectionPlan authenticationRequestServiceSelectionStrategies;

    private final ConfigurableApplicationContext applicationContext;

    private final AuditableExecution registeredServiceAccessStrategyEnforcer;

    private final CasConfigurationProperties casProperties;

    private final CasCookieBuilder ticketGrantingTicketCookieGenerator;

    private final List<ArgumentExtractor> argumentExtractors;

    private final PrincipalFactory principalFactory;

    private final SingleSignOnParticipationStrategy singleSignOnParticipationStrategy;

    private final AuthenticationEventExecutionPlan authenticationEventExecutionPlan;

    private final MultifactorAuthenticationContextValidator authenticationContextValidator;
}
