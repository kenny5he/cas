/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.microfish.it.account.cas.acct.web;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * JSON response for the non-WebFlow mobile login endpoint.
 *
 * @author kenny
 * @since 7.3.0
 */
@Getter
@Builder
public class MobileLoginResponse {
    private final boolean success;
    private final String username;
    private final String tgt;
    private final String serviceTicket;
    private final Map<String, Object> attributes;
    private final String error;
}
