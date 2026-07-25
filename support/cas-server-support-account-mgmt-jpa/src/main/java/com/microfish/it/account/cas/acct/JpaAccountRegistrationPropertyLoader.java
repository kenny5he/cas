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

package com.microfish.it.account.cas.acct;

import lombok.RequiredArgsConstructor;
import org.apereo.cas.acct.AccountRegistrationProperty;
import org.apereo.cas.acct.AccountRegistrationPropertyLoader;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JpaAccountRegistrationPropertyLoader implements AccountRegistrationPropertyLoader {

    private final RegistrationPropertyService registeredSelectionService;

    @Override
    public Map<String, AccountRegistrationProperty> load() {
        List<ExtAccountRegistrationProperty> properties = registeredSelectionService.find();
        return properties.stream()
            .collect(Collectors.toMap(ExtAccountRegistrationProperty::getName, property -> {
                property.setValues(Collections.emptyList());
                return property;
            }));
    }

    @Override
    public AccountRegistrationPropertyLoader store(Map<String, AccountRegistrationProperty> map) {
        registeredSelectionService.batch(map);
        return this;
    }
}
