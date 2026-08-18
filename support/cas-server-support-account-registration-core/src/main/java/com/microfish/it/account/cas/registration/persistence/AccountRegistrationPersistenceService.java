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

package com.microfish.it.account.cas.registration.persistence;

import org.apereo.cas.acct.AccountRegistrationRequest;

/**
 * Persistence boundary for registered accounts.
 *
 * @author kenny
 * @since 7.3.0
 */
public interface AccountRegistrationPersistenceService {

    /**
     * Determine whether a username already exists.
     *
     * @param username username
     * @return true when the username exists
     */
    boolean usernameExists(String username);

    /**
     * Determine whether an email address already exists.
     *
     * @param email email address
     * @return true when the address exists
     */
    boolean emailExists(String email);

    /**
     * Determine whether a phone number already exists.
     *
     * @param phone phone number
     * @return true when the number exists
     */
    boolean phoneExists(String phone);

    /**
     * Save a validated registration request.
     *
     * @param registrationRequest registration data
     * @param username resolved username
     */
    void save(AccountRegistrationRequest registrationRequest, String username);
}
