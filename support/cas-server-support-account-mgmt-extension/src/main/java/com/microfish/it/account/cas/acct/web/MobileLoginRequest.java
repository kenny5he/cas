/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.microfish.it.account.cas.acct.web;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JSON request for the non-WebFlow mobile login endpoint.
 *
 * @author kenny
 * @since 7.3.0
 */
@Getter
@Setter
@NoArgsConstructor
public class MobileLoginRequest {
    /** Country calling code, with or without a leading plus sign. */
    private String callingCode;

    /** Mobile phone number. Separators are ignored by the controller. */
    private String phoneNumber;

    /** Account password. */
    private String password;

    /** Optional SMS/passwordless verification code. */
    private String verificationCode;

    /** Optional registered CAS service URL. */
    private String service;

    /** Optional remember-me request. */
    private boolean rememberMe;
}
