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
import lombok.val;
import org.apereo.cas.acct.AccountRegistrationRequest;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.multitenancy.TenantExtractor;
import org.apereo.cas.notifications.CommunicationsManager;
import org.apereo.cas.notifications.mail.EmailCommunicationResult;
import org.apereo.cas.notifications.mail.EmailMessageRequest;
import org.apereo.cas.notifications.sms.SmsRequest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the email and phone account-registration code services.
 *
 * @author kenny
 * @since 7.3.0
 */
@Tag("Simple")
class AccountRegistrationCodeServiceTests {
    private static final Instant CURRENT_TIME = Instant.parse("2026-08-16T08:00:00Z");

    private static final String CODE = "A1B2C3";

    private final Clock clock = Clock.fixed(CURRENT_TIME, ZoneOffset.UTC);

    private final AccountRegistrationCodeGenerator generator = () -> CODE;

    private final AccountRegistrationCodeStore codeStore = mock(AccountRegistrationCodeStore.class);

    private final CommunicationsManager communicationsManager = mock(CommunicationsManager.class);

    private final TenantExtractor tenantExtractor = mock(TenantExtractor.class);

    private final CasConfigurationProperties casProperties = new CasConfigurationProperties();

    private final HttpServletRequest httpRequest = new MockHttpServletRequest();

    @Test
    void verifyEmailCodeExpiresAfterFiveMinutes() {
        when(communicationsManager.isMailSenderDefined()).thenReturn(true);
        when(communicationsManager.email(any(EmailMessageRequest.class)))
            .thenReturn(EmailCommunicationResult.builder().success(true).build());
        when(tenantExtractor.extract(httpRequest)).thenReturn(Optional.empty());
        when(codeStore.store(any(), any(), any(), any())).thenReturn("email-code-id");
        val service = new EmailAccountRegistrationCodeService(
            generator, codeStore, communicationsManager, casProperties, tenantExtractor, clock);
        val request = new AccountRegistrationRequest(Map.of("email", " User@Example.ORG "));

        service.createCode(request, httpRequest);

        verify(codeStore).store(AccountRegistrationType.EMAIL, "user@example.org", CODE,
            CURRENT_TIME.plus(AccountRegistrationCodeService.CODE_EXPIRATION));
        val requestCaptor = ArgumentCaptor.forClass(EmailMessageRequest.class);
        verify(communicationsManager).email(requestCaptor.capture());
        assertTrue(requestCaptor.getValue().getBody().contains(CODE));
        verify(codeStore, never()).invalidate("email-code-id");
    }

    @Test
    void verifyPhoneCodeValidationConsumesCode() {
        when(codeStore.consume(AccountRegistrationType.PHONE, "+8613800138000", CODE, CURRENT_TIME))
            .thenReturn(true);
        val service = new PhoneAccountRegistrationCodeService(
            generator, codeStore, communicationsManager, casProperties, tenantExtractor, clock);
        val request = new AccountRegistrationRequest(Map.of(
            "phone", "+86 (138) 0013-8000",
            AccountRegistrationCodeService.PROPERTY_VERIFICATION_CODE, CODE));

        service.validateCode(request);

        verify(codeStore).consume(AccountRegistrationType.PHONE, "+8613800138000", CODE, CURRENT_TIME);
    }

    @Test
    void verifySmsDeliveryFailureInvalidatesStoredCode() {
        casProperties.getAccountRegistration().getSms().setText("Registration code: ${code}");
        when(communicationsManager.isSmsSenderDefined()).thenReturn(true);
        when(communicationsManager.sms(any(SmsRequest.class))).thenReturn(false);
        when(tenantExtractor.extract(httpRequest)).thenReturn(Optional.empty());
        when(codeStore.store(eq(AccountRegistrationType.PHONE), any(), eq(CODE), any()))
            .thenReturn("sms-code-id");
        val service = new PhoneAccountRegistrationCodeService(
            generator, codeStore, communicationsManager, casProperties, tenantExtractor, clock);
        val request = new AccountRegistrationRequest(Map.of("phone", "+8613800138000"));

        assertThrows(AccountRegistrationCodeException.class, () -> service.createCode(request, httpRequest));

        verify(codeStore).store(AccountRegistrationType.PHONE, "+8613800138000", CODE,
            CURRENT_TIME.plus(AccountRegistrationCodeService.CODE_EXPIRATION));
        verify(codeStore).invalidate("sms-code-id");
    }

    @Test
    void verifyExpiredOrIncorrectCodeIsRejected() {
        val service = new EmailAccountRegistrationCodeService(
            generator, codeStore, communicationsManager, casProperties, tenantExtractor, clock);
        val request = new AccountRegistrationRequest(Map.of(
            "email", "user@example.org",
            AccountRegistrationCodeService.PROPERTY_VERIFICATION_CODE, CODE));
        when(codeStore.consume(AccountRegistrationType.EMAIL, "user@example.org", CODE, CURRENT_TIME))
            .thenReturn(false);

        val exception = assertThrows(AccountRegistrationCodeException.class, () -> service.validateCode(request));
        assertTrue(exception.getMessage().contains("expired"));
    }
}
