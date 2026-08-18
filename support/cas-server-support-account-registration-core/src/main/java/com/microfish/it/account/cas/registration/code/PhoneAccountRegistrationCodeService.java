/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.microfish.it.account.cas.registration.code;

import com.microfish.it.account.cas.enumate.AccountRegistrationType;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.acct.AccountRegistrationRequest;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.multitenancy.TenantDefinition;
import org.apereo.cas.multitenancy.TenantExtractor;
import org.apereo.cas.notifications.CommunicationsManager;
import org.apereo.cas.notifications.sms.SmsBodyBuilder;
import org.apereo.cas.notifications.sms.SmsRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.time.Clock;
import java.util.List;
import java.util.Map;

/**
 * Creates, sends and validates phone registration codes using SMS.
 *
 * @author kenny
 * @since 7.3.0
 */
@RequiredArgsConstructor
public class PhoneAccountRegistrationCodeService implements AccountRegistrationCodeService {
    private final AccountRegistrationCodeGenerator codeGenerator;

    private final AccountRegistrationCodeStore codeStore;

    private final CommunicationsManager communicationsManager;

    private final CasConfigurationProperties casProperties;

    private final TenantExtractor tenantExtractor;

    private final Clock clock;

    @Override
    public AccountRegistrationType getRegistrationType() {
        return AccountRegistrationType.PHONE;
    }

    @Override
    public void createCode(final AccountRegistrationRequest registrationRequest,
                           final HttpServletRequest httpRequest) {
        if (!communicationsManager.isSmsSenderDefined()) {
            throw new AccountRegistrationCodeException("Account registration SMS sender is not configured");
        }
        val phone = normalizePhone(registrationRequest.getPhone());
        val code = codeGenerator.generate();
        val identifier = codeStore.store(getRegistrationType(), phone, code,
            clock.instant().plus(CODE_EXPIRATION));
        var delivered = false;
        try {
            val smsProperties = casProperties.getAccountRegistration().getSms();
            val parameters = Map.<String, Object>of(
                "code", code,
                "url", code,
                "expirationMinutes", CODE_EXPIRATION.toMinutes());
            val text = SmsBodyBuilder.builder()
                .properties(smsProperties)
                .parameters(parameters)
                .build()
                .get();
            val smsRequest = SmsRequest.builder()
                .from(smsProperties.getFrom())
                .to(List.of(phone))
                .tenant(resolveTenant(httpRequest))
                .text(text)
                .build();
            delivered = communicationsManager.sms(smsRequest);
            if (!delivered) {
                throw new AccountRegistrationCodeException("Unable to send account registration code by SMS");
            }
        } finally {
            if (!delivered) {
                codeStore.invalidate(identifier);
            }
        }
    }

    @Override
    public void validateCode(final AccountRegistrationRequest registrationRequest) {
        val phone = normalizePhone(registrationRequest.getPhone());
        val code = getSubmittedCode(registrationRequest);
        if (!codeStore.consume(getRegistrationType(), phone, code, clock.instant())) {
            throw new AccountRegistrationCodeException("The phone verification code is invalid or has expired");
        }
    }

    private String normalizePhone(final String phone) {
        if (StringUtils.isBlank(phone)) {
            throw new AccountRegistrationCodeException("Phone number is required for phone registration");
        }
        return StringUtils.trim(phone).replaceAll("[\\s()-]", StringUtils.EMPTY);
    }

    private static String getSubmittedCode(final AccountRegistrationRequest registrationRequest) {
        val code = registrationRequest.getProperty(PROPERTY_VERIFICATION_CODE, String.class);
        if (StringUtils.isBlank(code)) {
            throw new AccountRegistrationCodeException("Phone verification code is required");
        }
        return StringUtils.trim(code);
    }

    private String resolveTenant(final HttpServletRequest httpRequest) {
        return tenantExtractor.extract(httpRequest)
            .map(TenantDefinition::getId)
            .orElse(StringUtils.EMPTY);
    }
}
