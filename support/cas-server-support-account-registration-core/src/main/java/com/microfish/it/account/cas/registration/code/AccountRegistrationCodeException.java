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

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Indicates that a registration verification code cannot be created or validated.
 *
 * @author kenny
 * @since 7.3.0
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountRegistrationCodeException extends RuntimeException {

    public AccountRegistrationCodeException(final String message) {
        super(message);
    }

    public AccountRegistrationCodeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
