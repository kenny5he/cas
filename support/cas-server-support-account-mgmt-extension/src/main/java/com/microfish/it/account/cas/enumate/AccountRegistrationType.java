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

package com.microfish.it.account.cas.enumate;

/**
 * @author kenny.he
 * @since 2026/08/16
 */
public enum AccountRegistrationType {

    EMAIL("email", "邮箱注册"),
    PHONE("phone", "手机注册"),
    ;

    private AccountRegistrationType(final String code, final String name) {
        this.name = name;
        this.code = code;
    }

    private String code;

    private String name;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static AccountRegistrationType getByCode(final String code) {
        for (AccountRegistrationType registrationType : AccountRegistrationType.values()) {
            if (registrationType.getCode().equals(code)) {
                return registrationType;
            }
        }
        throw new IllegalArgumentException("No registration type with code " + code);
    }
}
