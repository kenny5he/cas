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

import org.apereo.cas.acct.AccountRegistrationService;
import org.apereo.cas.configuration.features.CasFeatureModule;
import org.apereo.cas.util.spring.boot.ConditionalOnFeatureEnabled;

import com.microfish.it.account.cas.acct.web.AccountRegistrationController;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for account-registration MVC endpoints.
 *
 * @author kenny
 * @since 7.3.0
 */
@AutoConfiguration
@ConditionalOnFeatureEnabled(feature = CasFeatureModule.FeatureCatalog.AccountRegistration)
public class CasAccountManagementAutoConfiguration {
    /**
     * Create the account-registration MVC controller.
     *
     * @param accountRegistrationService account registration service
     * @return controller
     */
    @Bean
    @ConditionalOnMissingBean(name = "accountRegistrationModelAndViewController")
    public AccountRegistrationController accountRegistrationModelAndViewController(
        @Qualifier(AccountRegistrationService.BEAN_NAME)
        final AccountRegistrationService accountRegistrationService) {
        return new AccountRegistrationController(accountRegistrationService);
    }
}
