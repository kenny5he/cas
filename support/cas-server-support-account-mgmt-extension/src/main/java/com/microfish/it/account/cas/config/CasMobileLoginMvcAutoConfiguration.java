/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.microfish.it.account.cas.config;

import com.microfish.it.account.cas.acct.web.DefaultMobileLoginUsernameBuilder;
import com.microfish.it.account.cas.acct.web.MobileLoginController;
import com.microfish.it.account.cas.acct.web.MobileLoginUsernameBuilder;
import com.microfish.it.account.cas.acct.web.MobileVerificationCodeController;
import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.api.PasswordlessTokenRepository;
import org.apereo.cas.api.PasswordlessUserAccountStore;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.principal.ServiceFactory;
import org.apereo.cas.authentication.principal.WebApplicationService;
import org.apereo.cas.configuration.features.CasFeatureModule;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.notifications.CommunicationsManager;
import org.apereo.cas.throttle.AuthenticationThrottlingExecutionPlan;
import org.apereo.cas.util.spring.RefreshableHandlerInterceptor;
import org.apereo.cas.util.spring.boot.ConditionalOnFeatureEnabled;
import org.apereo.cas.web.CasWebSecurityConfigurer;
import org.apereo.cas.web.cookie.CasCookieBuilder;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.Nonnull;

import java.util.List;

/**
 * Auto-configuration for non-WebFlow mobile login MVC endpoints.
 *
 * @author kenny
 * @since 7.3.0
 */
@AutoConfiguration
@ConditionalOnFeatureEnabled(feature = CasFeatureModule.FeatureCatalog.Authentication)
public class CasMobileLoginMvcAutoConfiguration {
    /**
     * Register the default phone-to-username normalizer.
     *
     * @return username builder
     */
    @Bean(name = MobileLoginUsernameBuilder.BEAN_NAME)
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    @ConditionalOnMissingBean(name = MobileLoginUsernameBuilder.BEAN_NAME)
    public MobileLoginUsernameBuilder mobileLoginUsernameBuilder() {
        return new DefaultMobileLoginUsernameBuilder();
    }

    /**
     * Register the mobile MVC controller.
     *
     * @param authenticationSystemSupport authentication facade
     * @param centralAuthenticationService ticket service
     * @param serviceFactory service factory
     * @param usernameBuilder username normalizer
     * @param ticketGrantingTicketCookieGenerator TGC builder
     * @return controller
     */
    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    @ConditionalOnMissingBean(name = "mobileLoginController")
    public MobileLoginController mobileLoginController(
        @Qualifier(AuthenticationSystemSupport.BEAN_NAME)
        final AuthenticationSystemSupport authenticationSystemSupport,
        @Qualifier(CentralAuthenticationService.BEAN_NAME)
        final CentralAuthenticationService centralAuthenticationService,
        @Qualifier(WebApplicationService.BEAN_NAME_FACTORY)
        final ServiceFactory<WebApplicationService> serviceFactory,
        @Qualifier(MobileLoginUsernameBuilder.BEAN_NAME)
        final MobileLoginUsernameBuilder usernameBuilder,
        @Qualifier(CasCookieBuilder.BEAN_NAME_TICKET_GRANTING_COOKIE_BUILDER)
        final CasCookieBuilder ticketGrantingTicketCookieGenerator) {
        return new MobileLoginController(authenticationSystemSupport, centralAuthenticationService,
            serviceFactory, usernameBuilder, ticketGrantingTicketCookieGenerator);
    }

    /**
     * Register the passwordless SMS code endpoint when the CAS passwordless
     * account and token repositories are available.
     *
     * @param accountStore passwordless account store
     * @param tokenRepository passwordless token store
     * @param communicationsManager notification manager
     * @param casProperties CAS settings
     * @param usernameBuilder username normalizer
     * @return controller
     */
    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    @ConditionalOnMissingBean(name = "mobileVerificationCodeController")
    @ConditionalOnBean({PasswordlessUserAccountStore.class, PasswordlessTokenRepository.class, CommunicationsManager.class})
    public MobileVerificationCodeController mobileVerificationCodeController(
        @Qualifier(PasswordlessUserAccountStore.BEAN_NAME)
        final PasswordlessUserAccountStore accountStore,
        @Qualifier(PasswordlessTokenRepository.BEAN_NAME)
        final PasswordlessTokenRepository tokenRepository,
        @Qualifier(CommunicationsManager.BEAN_NAME)
        final CommunicationsManager communicationsManager,
        final CasConfigurationProperties casProperties,
        @Qualifier(MobileLoginUsernameBuilder.BEAN_NAME)
        final MobileLoginUsernameBuilder usernameBuilder) {
        return new MobileVerificationCodeController(accountStore, tokenRepository,
            communicationsManager, casProperties, usernameBuilder);
    }

    /**
     * Exclude the JSON login endpoint from the form-login security rules.
     *
     * @return security configurer
     */
    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    @ConditionalOnMissingBean(name = "mobileLoginEndpointConfigurer")
    public CasWebSecurityConfigurer<Void> mobileLoginEndpointConfigurer() {
        return new CasWebSecurityConfigurer<>() {
            @Override
            public List<String> getIgnoredEndpoints() {
                return List.of("/api/mobile-login");
            }
        };
    }

    /**
     * Apply the configured CAS authentication throttling interceptors to the
     * mobile login endpoint.
     *
     * @param executionPlan throttle execution plan
     * @return MVC configurer
     */
    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    @ConditionalOnMissingBean(name = "mobileLoginThrottlingWebMvcConfigurer")
    public WebMvcConfigurer mobileLoginThrottlingWebMvcConfigurer(
        @Qualifier(AuthenticationThrottlingExecutionPlan.BEAN_NAME)
        final ObjectProvider<AuthenticationThrottlingExecutionPlan> executionPlan) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(@Nonnull final InterceptorRegistry registry) {
                executionPlan.ifAvailable(plan -> registry
                    .addInterceptor(new RefreshableHandlerInterceptor(plan::getAuthenticationThrottleInterceptors))
                    .order(0)
                    .addPathPatterns("/api/mobile-login", "/api/mobile-login/**"));
            }
        };
    }
}
