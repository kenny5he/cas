/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.microfish.it.account.cas.acct.web;

import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * Default phone username normalizer.
 *
 * @author kenny
 * @since 7.3.0
 */
public class DefaultMobileLoginUsernameBuilder implements MobileLoginUsernameBuilder {
    private static final Pattern NON_DIGIT = Pattern.compile("[^0-9]");

    @Override
    public String build(final MobileLoginRequest request) {
        val phone = NON_DIGIT.matcher(StringUtils.defaultString(request.getPhoneNumber())).replaceAll("");
        if (phone.isEmpty()) {
            throw new IllegalArgumentException("Phone number must contain digits");
        }
        val callingCode = NON_DIGIT.matcher(StringUtils.defaultString(request.getCallingCode())).replaceAll("");
        return StringUtils.isBlank(callingCode) ? phone : "+" + callingCode + phone;
    }
}
