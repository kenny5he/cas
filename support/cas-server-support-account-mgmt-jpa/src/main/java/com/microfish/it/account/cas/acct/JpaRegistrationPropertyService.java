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

import com.microfish.it.account.cas.acct.entity.JpaRegistrationPropertyEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apereo.cas.acct.AccountRegistrationProperty;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JpaRegistrationPropertyService implements RegistrationPropertyService {

    public static final String PERSISTENCE_UNIT_NAME = "jpaRegistrationPropertyContext";

    @Getter
    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    private EntityManager entityManager;


    @Override
    public void save(ExtAccountRegistrationProperty accountRegistrationProperty) {

    }

    @Override
    public void batch(Map<String, AccountRegistrationProperty> map) {
        map.forEach((key, value) -> {
            if (value instanceof ExtAccountRegistrationProperty extValue) {
                save(extValue);
            }
        });
    }

    @Override
    public List<ExtAccountRegistrationProperty> find() {

        return Collections.emptyList();
    }
}
