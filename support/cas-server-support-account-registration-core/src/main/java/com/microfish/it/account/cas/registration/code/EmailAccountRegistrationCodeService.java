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
import org.apereo.cas.notifications.mail.EmailMessageBodyBuilder;
import org.apereo.cas.notifications.mail.EmailMessageRequest;
import org.springframework.web.servlet.support.RequestContextUtils;
import jakarta.servlet.http.HttpServletRequest;

import java.time.Clock;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Creates, sends and validates email registration codes.
 *
 * @author kenny
 * @since 7.3.0
 */
@RequiredArgsConstructor
public class EmailAccountRegistrationCodeService implements AccountRegistrationCodeService {
    private final AccountRegistrationCodeGenerator codeGenerator;

    private final AccountRegistrationCodeStore codeStore;

    private final CommunicationsManager communicationsManager;

    private final CasConfigurationProperties casProperties;

    private final TenantExtractor tenantExtractor;

    private final Clock clock;

    @Override
    public AccountRegistrationType getRegistrationType() {
        return AccountRegistrationType.EMAIL;
    }

    @Override
    public void createCode(final AccountRegistrationRequest registrationRequest,
                           final HttpServletRequest httpRequest) {
        if (!communicationsManager.isMailSenderDefined()) {
            throw new AccountRegistrationCodeException("Account registration email sender is not configured");
        }
        val email = normalizeEmail(registrationRequest.getEmail());
        val code = codeGenerator.generate();
        val identifier = codeStore.store(getRegistrationType(), email, code,
            clock.instant().plus(CODE_EXPIRATION));
        var delivered = false;
        try {
            val mailProperties = casProperties.getAccountRegistration().getMail();
            val parameters = Map.<String, Object>of(
                "code", code,
                "url", code,
                "expirationMinutes", CODE_EXPIRATION.toMinutes());
            val locale = resolveLocale(httpRequest);
            val body = EmailMessageBodyBuilder.builder()
                .properties(mailProperties)
                .parameters(parameters)
                .locale(locale)
                .build()
                .get();
            val emailRequest = EmailMessageRequest.builder()
                .emailProperties(mailProperties)
                .locale(locale.orElseGet(Locale::getDefault))
                .to(List.of(email))
                .tenant(resolveTenant(httpRequest))
                .context(parameters)
                .body(body)
                .build();
            delivered = communicationsManager.email(emailRequest).isSuccess();
            if (!delivered) {
                throw new AccountRegistrationCodeException("Unable to send account registration code by email");
            }
        } finally {
            if (!delivered) {
                codeStore.invalidate(identifier);
            }
        }
    }

    @Override
    public void validateCode(final AccountRegistrationRequest registrationRequest) {
        val email = normalizeEmail(registrationRequest.getEmail());
        val code = getSubmittedCode(registrationRequest);
        if (!codeStore.consume(getRegistrationType(), email, code, clock.instant())) {
            throw new AccountRegistrationCodeException("The email verification code is invalid or has expired");
        }
    }

    private String normalizeEmail(final String email) {
        if (StringUtils.isBlank(email)) {
            throw new AccountRegistrationCodeException("Email address is required for email registration");
        }
        return StringUtils.lowerCase(StringUtils.trim(email), Locale.ROOT);
    }

    private static String getSubmittedCode(final AccountRegistrationRequest registrationRequest) {
        val code = registrationRequest.getProperty(PROPERTY_VERIFICATION_CODE, String.class);
        if (StringUtils.isBlank(code)) {
            throw new AccountRegistrationCodeException("Email verification code is required");
        }
        return StringUtils.trim(code);
    }

    private Optional<Locale> resolveLocale(final HttpServletRequest httpRequest) {
        return Optional.ofNullable(RequestContextUtils.getLocaleResolver(httpRequest))
            .map(resolver -> resolver.resolveLocale(httpRequest));
    }

    private String resolveTenant(final HttpServletRequest httpRequest) {
        return tenantExtractor.extract(httpRequest)
            .map(TenantDefinition::getId)
            .orElse(StringUtils.EMPTY);
    }
}
