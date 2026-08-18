/**
 * Copyright 2026 - Ren Jian Yan Huo
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.microfish.it.account.cas.registration.enumate;

import lombok.val;

/**
 * Supported account-registration communication channels.
 *
 * @author kenny.he
 * @since 7.3.0
 */
public enum AccountRegistrationType {

    /** Email registration. */
    EMAIL("mail", "邮箱"),

    /** Phone registration. */
    PHONE("phone", "手机号");

    private final String code;

    private final String name;

    AccountRegistrationType(final String code, final String name) {
        this.name = name;
        this.code = code;
    }

    /**
     * Get the registration type code.
     *
     * @return registration type code
     */
    public String getCode() {
        return code;
    }

    /**
     * Get the registration type display name.
     *
     * @return display name
     */
    public String getName() {
        return name;
    }

    /**
     * Resolve a registration type by its code or enum name.
     *
     * @param code registration type code
     * @return registration type
     * @throws IllegalArgumentException when the code is unsupported
     */
    public static AccountRegistrationType getByCode(final String code) {
        for (val registrationType : AccountRegistrationType.values()) {
            if (code != null && (code.equalsIgnoreCase(registrationType.getCode())
                || code.equalsIgnoreCase(registrationType.name()))) {
                return registrationType;
            }
        }
        throw new IllegalArgumentException("No registration type with code " + code);
    }
}
