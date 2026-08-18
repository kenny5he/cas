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
import org.apereo.cas.acct.AccountRegistrationRequest;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Routes verification-code operations to a channel-specific service.
 *
 * @author kenny
 * @since 7.3.0
 */
public interface AccountRegistrationCodeManager {

    /**
     * Create and send a code.
     *
     * @param registrationRequest registration data
     * @param registrationType registration channel
     * @param httpRequest current HTTP request
     */
    void createCode(AccountRegistrationRequest registrationRequest,
                    AccountRegistrationType registrationType,
                    HttpServletRequest httpRequest);

    /**
     * Validate and consume a code.
     *
     * @param registrationRequest registration data
     * @param registrationType registration channel
     */
    void validateCode(AccountRegistrationRequest registrationRequest, AccountRegistrationType registrationType);
}
