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

package com.microfish.it.login.cas.pac4j.configuration;

import org.apereo.cas.authentication.CasSSLContext;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.features.CasFeatureModule;
import org.apereo.cas.util.spring.boot.ConditionalOnFeatureEnabled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ScopedProxyMode;

import com.microfish.it.login.cas.pac4j.ExtPac4jAuthenticationProperties;
import com.microfish.it.login.cas.pac4j.web.ExtensionDelegatedClientOidcBuilder;
import org.apereo.cas.support.pac4j.authentication.clients.ConfigurableDelegatedClientBuilder;

/**
 * 覆盖: DelegatedAuthenticationOidcConfiguration 类配置
 */
@ConditionalOnFeatureEnabled(feature = CasFeatureModule.FeatureCatalog.DelegatedAuthentication, module = "oidc")
@Configuration(value = "DelegatedAuthenticationOidcConfiguration", proxyBeanMethods = false)
@EnableConfigurationProperties({CasConfigurationProperties.class, ExtPac4jAuthenticationProperties.class})
public class ExtensionDelegatedAuthenticationOidcConfiguration {

    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    @ConditionalOnMissingBean(name = "delegatedOidcClientBuilder")
    public ConfigurableDelegatedClientBuilder delegatedOidcClientBuilder(
            @Qualifier(CasSSLContext.BEAN_NAME)
        final CasSSLContext casSslContext,
            @Autowired ExtPac4jAuthenticationProperties ext) {
        return new ExtensionDelegatedClientOidcBuilder(casSslContext, ext);
    }
}
