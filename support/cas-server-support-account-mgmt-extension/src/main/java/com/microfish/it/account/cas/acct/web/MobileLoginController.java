/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.microfish.it.account.cas.acct.web;

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.credential.OneTimePasswordCredential;
import org.apereo.cas.authentication.credential.RememberMeUsernamePasswordCredential;
import org.apereo.cas.authentication.principal.ServiceFactory;
import org.apereo.cas.authentication.principal.WebApplicationService;
import org.apereo.cas.util.LoggingUtils;
import org.apereo.cas.web.cookie.CasCookieBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;

/**
 * Non-WebFlow MVC endpoint for phone-number/password authentication.
 *
 * <p>The phone number is converted to the canonical username understood by the
 * configured CAS authentication handler. By default this is {@code +callingCode
 * phoneNumber}; deployments that store a different key can configure a custom
 * {@link MobileLoginUsernameBuilder} bean.</p>
 *
 * @author kenny
 * @since 7.3.0
 */
@RestController("mobileLoginController")
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/api/mobile-login", produces = MediaType.APPLICATION_JSON_VALUE)
public class MobileLoginController {
    private final AuthenticationSystemSupport authenticationSystemSupport;

    private final CentralAuthenticationService centralAuthenticationService;

    private final ServiceFactory<WebApplicationService> serviceFactory;

    private final MobileLoginUsernameBuilder usernameBuilder;

    private final CasCookieBuilder ticketGrantingTicketCookieGenerator;

    /**
     * Authenticate a mobile account and issue CAS tickets.
     *
     * @param loginRequest request body
     * @param request servlet request
     * @param response servlet response
     * @return ticket response
     * @throws Throwable when CAS authentication or ticket issuance fails
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MobileLoginResponse> login(@RequestBody final MobileLoginRequest loginRequest,
                                                     final HttpServletRequest request,
                                                     final HttpServletResponse response) throws Throwable {
        validate(loginRequest);
        val username = usernameBuilder.build(loginRequest);
        val credential = buildCredential(loginRequest, username);

        val service = StringUtils.isBlank(loginRequest.getService())
            ? null : serviceFactory.createService(loginRequest.getService(), request);
        val authenticationResult = authenticationSystemSupport.finalizeAuthenticationTransaction(service, credential);
        val tgt = centralAuthenticationService.createTicketGrantingTicket(authenticationResult);
        ticketGrantingTicketCookieGenerator.addCookie(request, response, loginRequest.isRememberMe(), tgt.getId());
        String serviceTicket = null;
        if (service != null) {
            serviceTicket = centralAuthenticationService.grantServiceTicket(tgt.getId(), service, authenticationResult).getId();
        }
        val principal = authenticationResult.getAuthentication().getPrincipal();
        val attributes = new LinkedHashMap<String, Object>(principal.getAttributes());
        return ResponseEntity.status(HttpStatus.CREATED).body(MobileLoginResponse.builder()
            .success(true)
            .username(principal.getId())
            .tgt(tgt.getId())
            .serviceTicket(serviceTicket)
            .attributes(attributes)
            .build());
    }

    /**
     * Map authentication and request failures to a stable JSON response.
     *
     * @param exception failure
     * @return JSON error
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<MobileLoginResponse> handleException(final Throwable exception) {
        LoggingUtils.error(LOGGER, exception);
        val status = exception instanceof IllegalArgumentException ? HttpStatus.BAD_REQUEST : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(MobileLoginResponse.builder()
            .success(false)
            .error(status == HttpStatus.BAD_REQUEST
                ? StringUtils.defaultIfBlank(exception.getMessage(), "Invalid request")
                : "Authentication failed")
            .build());
    }

    private static void validate(final MobileLoginRequest loginRequest) {
        if (loginRequest == null) {
            throw new IllegalArgumentException("Login request is required");
        }
        if (StringUtils.isBlank(loginRequest.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number is required");
        }
        if (StringUtils.isAllBlank(loginRequest.getPassword(), loginRequest.getVerificationCode())) {
            throw new IllegalArgumentException("Password or verification code is required");
        }
    }

    private static Credential buildCredential(final MobileLoginRequest loginRequest, final String username) {
        if (StringUtils.isNotBlank(loginRequest.getVerificationCode())) {
            return new OneTimePasswordCredential(username, loginRequest.getVerificationCode());
        }
        val credential = new RememberMeUsernamePasswordCredential(loginRequest.isRememberMe());
        credential.setUsername(username);
        credential.assignPassword(loginRequest.getPassword());
        return credential;
    }
}
