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
import jakarta.servlet.http.HttpServletRequest;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Default {@link AccountRegistrationCodeManager} implementation.
 *
 * @author kenny
 * @since 7.3.0
 */
public class DefaultAccountRegistrationCodeManager implements AccountRegistrationCodeManager {
    private final Map<AccountRegistrationType, AccountRegistrationCodeService> services;

    public DefaultAccountRegistrationCodeManager(final List<AccountRegistrationCodeService> services) {
        this.services = new EnumMap<>(AccountRegistrationType.class);
        services.forEach(service -> {
            val previous = this.services.put(service.getRegistrationType(), service);
            if (previous != null) {
                throw new IllegalStateException("Multiple verification-code services support " + service.getRegistrationType());
            }
        });
    }

    @Override
    public void createCode(final AccountRegistrationRequest registrationRequest,
                           final AccountRegistrationType registrationType,
                           final HttpServletRequest httpRequest) {
        getService(registrationType).createCode(registrationRequest, httpRequest);
    }

    @Override
    public void validateCode(final AccountRegistrationRequest registrationRequest,
                             final AccountRegistrationType registrationType) {
        getService(registrationType).validateCode(registrationRequest);
    }

    private AccountRegistrationCodeService getService(final AccountRegistrationType registrationType) {
        val service = services.get(registrationType);
        if (service == null) {
            throw new AccountRegistrationCodeException("Unsupported registration type: " + registrationType);
        }
        return service;
    }
}
