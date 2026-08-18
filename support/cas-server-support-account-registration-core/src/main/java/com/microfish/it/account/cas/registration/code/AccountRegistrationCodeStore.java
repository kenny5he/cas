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

import java.time.Instant;

/**
 * Persistence boundary for one-time account-registration codes.
 *
 * @author kenny
 * @since 7.3.0
 */
public interface AccountRegistrationCodeStore {

    /**
     * Store a code and invalidate previously active codes for the same target.
     *
     * @param registrationType registration channel
     * @param target normalized email address or phone number
     * @param code verification code
     * @param expirationTime expiration time
     * @return opaque identifier for the stored code
     */
    String store(AccountRegistrationType registrationType, String target, String code, Instant expirationTime);

    /**
     * Invalidate a code, normally because delivery failed.
     *
     * @param identifier stored-code identifier
     */
    void invalidate(String identifier);

    /**
     * Atomically validate and consume a code.
     *
     * @param registrationType registration channel
     * @param target normalized email address or phone number
     * @param code submitted verification code
     * @param currentTime current time
     * @return whether a matching unexpired code was consumed
     */
    boolean consume(AccountRegistrationType registrationType, String target, String code, Instant currentTime);
}
