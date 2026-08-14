/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.microfish.it.account.cas.acct.web;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link DefaultMobileLoginUsernameBuilder}.
 *
 * @author kenny
 * @since 7.3.0
 */
class DefaultMobileLoginUsernameBuilderTests {
    private final DefaultMobileLoginUsernameBuilder builder = new DefaultMobileLoginUsernameBuilder();

    @Test
    void verifyInternationalPhoneNumber() {
        val request = new MobileLoginRequest();
        request.setCallingCode("+86");
        request.setPhoneNumber("138 0013-8000");
        assertEquals("+8613800138000", builder.build(request));
    }

    @Test
    void verifyLocalPhoneNumber() {
        val request = new MobileLoginRequest();
        request.setPhoneNumber("13800138000");
        assertEquals("13800138000", builder.build(request));
    }

    @Test
    void verifyInvalidPhoneNumber() {
        val request = new MobileLoginRequest();
        request.setPhoneNumber("invalid");
        assertThrows(IllegalArgumentException.class, () -> builder.build(request));
    }
}
