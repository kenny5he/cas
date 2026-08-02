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

package com.microfish.it.account.cas.acct.web;

import org.apereo.cas.acct.AccountRegistrationProperty;
import org.apereo.cas.acct.AccountRegistrationRequest;
import org.apereo.cas.acct.AccountRegistrationResponse;
import org.apereo.cas.acct.AccountRegistrationService;
import org.apereo.cas.acct.provision.AccountRegistrationProvisioner;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link AccountRegistrationModelAndViewController}.
 *
 * @author kenny
 * @since 7.3.0
 */
class AccountRegistrationModelAndViewControllerTests {
    private AccountRegistrationService accountRegistrationService;

    private AccountRegistrationModelAndViewController controller;

    @BeforeEach
    void initialize() throws Throwable {
        accountRegistrationService = mock(AccountRegistrationService.class);
        val propertyLoader = mock(org.apereo.cas.acct.AccountRegistrationPropertyLoader.class);
        val username = AccountRegistrationProperty.builder()
            .name("username")
            .required(true)
            .order(0)
            .build();
        val email = AccountRegistrationProperty.builder()
            .name("email")
            .type("email")
            .pattern(".+@.+")
            .required(true)
            .order(1)
            .build();
        when(propertyLoader.load()).thenReturn(new LinkedHashMap<>(Map.of(
            "username", username,
            "email", email)));
        when(accountRegistrationService.getAccountRegistrationPropertyLoader()).thenReturn(propertyLoader);
        when(accountRegistrationService.getAccountRegistrationRequestValidator())
            .thenReturn(request -> { });
        when(accountRegistrationService.getAccountRegistrationUsernameBuilder())
            .thenReturn(AccountRegistrationRequest::getUsername);

        val provisioner = mock(AccountRegistrationProvisioner.class);
        when(provisioner.provision(any(AccountRegistrationRequest.class)))
            .thenReturn(AccountRegistrationResponse.success());
        when(accountRegistrationService.getAccountRegistrationProvisioner()).thenReturn(provisioner);
        when(accountRegistrationService.createToken(any(AccountRegistrationRequest.class))).thenReturn("token-value");
        controller = new AccountRegistrationModelAndViewController(accountRegistrationService);
    }

    @Test
    void verifyRegistrationProperties() {
        val modelAndView = controller.getRegistrationProperties();
        assertEquals(HttpStatus.OK, modelAndView.getStatus());
        assertTrue((Boolean) modelAndView.getModel().get("success"));
        assertNotNull(modelAndView.getModel().get("registrationProperties"));
    }

    @Test
    void verifyDirectRegistration() {
        val modelAndView = controller.register(Map.of(
            "username", "casuser",
            "email", "casuser@example.org",
            "password", "password"));
        assertEquals(HttpStatus.CREATED, modelAndView.getStatus());
        assertTrue((Boolean) modelAndView.getModel().get("success"));
        assertEquals("casuser", modelAndView.getModel().get("username"));
    }

    @Test
    void verifyInvalidRegistrationRequest() {
        val modelAndView = controller.register(Map.of("username", "casuser"));
        assertEquals(HttpStatus.BAD_REQUEST, modelAndView.getStatus());
        assertFalse((Boolean) modelAndView.getModel().get("success"));
        assertEquals("invalid_request", modelAndView.getModel().get("error"));
    }

    @Test
    void verifyRegistrationToken() {
        val modelAndView = controller.createRegistrationToken(Map.of(
            "username", "casuser",
            "email", "casuser@example.org"));
        assertEquals(HttpStatus.CREATED, modelAndView.getStatus());
        assertEquals("token-value", modelAndView.getModel().get("token"));
    }

    @Test
    void verifyInvalidActivationToken() throws Exception {
        when(accountRegistrationService.validateToken("invalid-token")).thenReturn(null);
        val request = mock(AccountRegistrationActivationRequest.class);
        when(request.getToken()).thenReturn("invalid-token");
        when(request.getProperties()).thenReturn(Map.of());

        val modelAndView = controller.activate(request);
        assertEquals(HttpStatus.BAD_REQUEST, modelAndView.getStatus());
        assertFalse((Boolean) modelAndView.getModel().get("success"));
    }
}
