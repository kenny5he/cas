/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.microfish.it.account.cas.acct.web;

import org.apereo.cas.api.PasswordlessAuthenticationRequest;
import org.apereo.cas.api.PasswordlessTokenRepository;
import org.apereo.cas.api.PasswordlessUserAccountStore;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.notifications.CommunicationsManager;
import org.apereo.cas.notifications.sms.SmsBodyBuilder;
import org.apereo.cas.notifications.sms.SmsRequest;

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

import java.util.List;
import java.util.Map;

/**
 * Non-WebFlow MVC endpoint that creates and sends an SMS passwordless token.
 *
 * @author kenny
 * @since 7.3.0
 */
@RestController("mobileVerificationCodeController")
@RequestMapping(path = "/api/mobile-login/code", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class MobileVerificationCodeController {
    private final PasswordlessUserAccountStore accountStore;

    private final PasswordlessTokenRepository tokenRepository;

    private final CommunicationsManager communicationsManager;

    private final CasConfigurationProperties casProperties;

    private final MobileLoginUsernameBuilder usernameBuilder;

    /**
     * Create and send a one-time verification code.
     *
     * @param request mobile account request
     * @return accepted response
     * @throws Throwable when the account cannot be found or the message cannot be sent
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MobileLoginResponse> sendCode(@RequestBody final MobileLoginRequest request) throws Throwable {
        if (request == null || StringUtils.isBlank(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number is required");
        }
        val username = usernameBuilder.build(request);
        val passwordlessRequest = PasswordlessAuthenticationRequest.builder()
            .username(username)
            .providedUsername(username)
            .build();
        val account = accountStore.findUser(passwordlessRequest);
        if (account.isEmpty() || StringUtils.isBlank(account.get().getPhone())) {
            LOGGER.info("Ignoring a mobile verification-code request for an unknown account");
            return accepted();
        }
        if (!communicationsManager.isSmsSenderDefined()) {
            throw new IllegalStateException("SMS sender is not configured");
        }

        val passwordlessAccount = account.get();
        val token = tokenRepository.createToken(passwordlessAccount, passwordlessRequest);
        val smsProperties = casProperties.getAuthn().getPasswordless().getTokens().getSms();
        val text = SmsBodyBuilder.builder()
            .properties(smsProperties)
            .parameters(Map.of("token", token.getToken()))
            .build()
            .get();
        val smsRequest = SmsRequest.builder()
            .from(smsProperties.getFrom())
            .to(List.of(passwordlessAccount.getPhone()))
            .text(text)
            .build();
        if (!communicationsManager.sms(smsRequest)) {
            throw new IllegalStateException("Unable to send the verification code");
        }
        tokenRepository.deleteTokens(passwordlessAccount.getUsername());
        tokenRepository.saveToken(passwordlessAccount, passwordlessRequest, token);
        LOGGER.info("Issued a mobile verification code for [{}]", passwordlessAccount.getUsername());
        return accepted();
    }

    /**
     * Return request and delivery failures as JSON.
     *
     * @param exception failure
     * @return JSON error response
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<MobileLoginResponse> handleException(final Throwable exception) {
        LOGGER.error("Unable to process mobile verification-code request", exception);
        val status = exception instanceof IllegalArgumentException
            ? HttpStatus.BAD_REQUEST : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(status).body(MobileLoginResponse.builder()
            .success(false)
            .error(status == HttpStatus.BAD_REQUEST
                ? StringUtils.defaultIfBlank(exception.getMessage(), "Invalid request")
                : "Verification code is unavailable")
            .build());
    }

    private static ResponseEntity<MobileLoginResponse> accepted() {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(MobileLoginResponse.builder()
            .success(true)
            .build());
    }
}
