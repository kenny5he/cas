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

package com.microfish.it.account.cas.registration.web.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Request used to complete an account registration using a registration token.
 *
 * @author kenny
 * @since 7.3.0
 */
@Getter
@Setter
@NoArgsConstructor
public class AccountRegistrationCodeRequest {
    /**
     * Kept for clients using the original request shape. The URL path is the
     * authoritative registration type.
     */
    private String registrationType;

    private final Map<String, Object> properties = new LinkedHashMap<>();
}
