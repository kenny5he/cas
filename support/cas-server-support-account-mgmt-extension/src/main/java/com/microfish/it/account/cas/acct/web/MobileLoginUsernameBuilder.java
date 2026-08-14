/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.microfish.it.account.cas.acct.web;

/**
 * Builds the username/key passed to the configured CAS authentication handlers.
 *
 * @author kenny
 * @since 7.3.0
 */
@FunctionalInterface
public interface MobileLoginUsernameBuilder {
    /** Default bean name. */
    String BEAN_NAME = "mobileLoginUsernameBuilder";

    /**
     * Build a canonical username from a mobile login request.
     *
     * @param request login request
     * @return canonical username
     */
    String build(MobileLoginRequest request);
}
