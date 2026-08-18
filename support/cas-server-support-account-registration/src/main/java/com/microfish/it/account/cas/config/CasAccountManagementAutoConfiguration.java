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

import com.microfish.it.account.cas.registration.code.AccountRegistrationCodeGenerator;
import com.microfish.it.account.cas.registration.code.AccountRegistrationCodeManager;
import com.microfish.it.account.cas.registration.code.AccountRegistrationCodeService;
import com.microfish.it.account.cas.registration.code.AccountRegistrationCodeStore;
import com.microfish.it.account.cas.registration.code.DefaultAccountRegistrationCodeManager;
import com.microfish.it.account.cas.registration.code.EmailAccountRegistrationCodeService;
import com.microfish.it.account.cas.registration.code.PhoneAccountRegistrationCodeService;
import com.microfish.it.account.cas.registration.persistence.AccountRegistrationPersistenceService;
import com.microfish.it.account.cas.registration.web.AccountRegistrationController;
import org.apereo.cas.acct.AccountRegistrationService;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.features.CasFeatureModule;
import org.apereo.cas.multitenancy.TenantExtractor;
import org.apereo.cas.notifications.CommunicationsManager;
import org.apereo.cas.util.RandomUtils;
import org.apereo.cas.util.spring.boot.ConditionalOnFeatureEnabled;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.util.List;

/**
 * Auto-configuration for account-registration MVC endpoints.
 *
 * @author kenny
 * @since 7.3.0
 */
@AutoConfiguration
@EnableConfigurationProperties(CasConfigurationProperties.class)
@ConditionalOnFeatureEnabled(feature = CasFeatureModule.FeatureCatalog.AccountRegistration)
@ConditionalOnBean({AccountRegistrationCodeStore.class, AccountRegistrationPersistenceService.class})
public class CasAccountManagementAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "accountRegistrationCodeGenerator")
    public AccountRegistrationCodeGenerator accountRegistrationCodeGenerator() {
        return new SecureRandomAccountRegistrationCodeGenerator(RandomUtils.getNativeInstance());
    }

    @Bean
    @ConditionalOnMissingBean(name = "accountRegistrationClock")
    public Clock accountRegistrationClock() {
        return Clock.systemUTC();
    }

    @Bean
    public AccountRegistrationCodeService emailAccountRegistrationCodeService(
        final AccountRegistrationCodeGenerator accountRegistrationCodeGenerator,
        final AccountRegistrationCodeStore accountRegistrationCodeStore,
        @Qualifier(CommunicationsManager.BEAN_NAME)
        final CommunicationsManager communicationsManager,
        final CasConfigurationProperties casProperties,
        @Qualifier(TenantExtractor.BEAN_NAME)
        final TenantExtractor tenantExtractor,
        @Qualifier("accountRegistrationClock")
        final Clock accountRegistrationClock) {
        return new EmailAccountRegistrationCodeService(
            accountRegistrationCodeGenerator, accountRegistrationCodeStore,
            communicationsManager, casProperties, tenantExtractor, accountRegistrationClock);
    }

    @Bean
    public AccountRegistrationCodeService phoneAccountRegistrationCodeService(
        final AccountRegistrationCodeGenerator accountRegistrationCodeGenerator,
        final AccountRegistrationCodeStore accountRegistrationCodeStore,
        @Qualifier(CommunicationsManager.BEAN_NAME)
        final CommunicationsManager communicationsManager,
        final CasConfigurationProperties casProperties,
        @Qualifier(TenantExtractor.BEAN_NAME)
        final TenantExtractor tenantExtractor,
        @Qualifier("accountRegistrationClock")
        final Clock accountRegistrationClock) {
        return new PhoneAccountRegistrationCodeService(
            accountRegistrationCodeGenerator, accountRegistrationCodeStore,
            communicationsManager, casProperties, tenantExtractor, accountRegistrationClock);
    }

    @Bean
    @ConditionalOnMissingBean(AccountRegistrationCodeManager.class)
    public AccountRegistrationCodeManager accountRegistrationCodeManager(
        final List<AccountRegistrationCodeService> accountRegistrationCodeServices) {
        return new DefaultAccountRegistrationCodeManager(accountRegistrationCodeServices);
    }

    @Bean
    public AccountRegistrationController accountRegistrationController(
        @Qualifier(AccountRegistrationService.BEAN_NAME)
        final AccountRegistrationService accountRegistrationService,
        final AccountRegistrationCodeManager accountRegistrationCodeManager,
        final AccountRegistrationPersistenceService accountRegistrationPersistenceService) {
        return new AccountRegistrationController(
            accountRegistrationService, accountRegistrationCodeManager, accountRegistrationPersistenceService);
    }
}
