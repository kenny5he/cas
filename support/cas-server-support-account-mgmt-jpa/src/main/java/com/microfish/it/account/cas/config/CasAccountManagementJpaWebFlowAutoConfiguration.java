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

package com.microfish.it.account.cas.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ScopedProxyMode;

import org.apereo.cas.acct.AccountRegistrationPropertyLoader;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.features.CasFeatureModule;
import org.apereo.cas.util.spring.boot.ConditionalOnFeatureEnabled;

import com.microfish.it.account.cas.acct.JpaAccountRegistrationPropertyLoader;
import com.microfish.it.account.cas.acct.JpaRegistrationPropertyService;
import com.microfish.it.account.cas.acct.RegistrationPropertyService;

@EnableConfigurationProperties(CasConfigurationProperties.class)
@ConditionalOnFeatureEnabled(feature = CasFeatureModule.FeatureCatalog.AccountRegistration)
@AutoConfiguration
public class CasAccountManagementJpaWebFlowAutoConfiguration {

    @Bean
    public RegistrationPropertyService jpaRegistrationSelection() {
        return new JpaRegistrationPropertyService();
    }

    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public AccountRegistrationPropertyLoader accountMgmtRegistrationPropertyLoader(RegistrationPropertyService registrationSelectionService) {
        return new JpaAccountRegistrationPropertyLoader(registrationSelectionService);
    }

}
