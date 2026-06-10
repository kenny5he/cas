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

package com.microfish.it.login.cas.pac4j.web;

import com.microfish.it.login.cas.pac4j.client.FeishuClient;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import org.pac4j.oauth.client.QQClient;
import org.pac4j.oauth.client.WechatClient;

import org.apereo.cas.authentication.CasSSLContext;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.pac4j.web.DelegatedClientOidcBuilder;
import org.apereo.cas.support.pac4j.authentication.clients.ConfigurableDelegatedClient;

import com.microfish.it.login.cas.pac4j.ExtPac4jAuthenticationProperties;
import com.microfish.it.login.cas.pac4j.client.WorkWechatClient;
import com.microfish.it.login.cas.pac4j.client.AlipayClient;

/**
 * 扩展
 *    1. 创建工作微信 client
 *
 * @author kenny.he
 * @since 2026/06/10
 */
@Slf4j
public class ExtensionDelegatedClientOidcBuilder extends DelegatedClientOidcBuilder {

    private ExtPac4jAuthenticationProperties extProperties;

    public ExtensionDelegatedClientOidcBuilder(CasSSLContext casSslContext) {
        super(casSslContext);
    }

    public ExtensionDelegatedClientOidcBuilder(CasSSLContext casSslContext, ExtPac4jAuthenticationProperties extProperties) {
        super(casSslContext);
        this.extProperties = extProperties;
    }

    @Override
    public List<ConfigurableDelegatedClient> build(final CasConfigurationProperties casProperties) {
        val newClients = super.build(casProperties);
        newClients.addAll(buildQQProviders(casProperties));
        newClients.addAll(buildWechatProviders(casProperties));
        newClients.addAll(buildWorkWechatProviders(casProperties));
        newClients.addAll(buildAlipayProviders(casProperties));
        newClients.addAll(buildWorkFeiShuProviders(casProperties));
        return newClients;
    }

    /**
     * 构建 QQ Client
     * @param casProperties 配置
     * @return QQClient
     */
    protected Collection<ConfigurableDelegatedClient> buildQQProviders(CasConfigurationProperties casProperties) {
        val qq = extProperties.getQq();
        if (qq.isEnabled() && StringUtils.isNotBlank(qq.getId()) && StringUtils.isNotBlank(qq.getSecret())) {
            val client = new QQClient(qq.getId(), qq.getSecret());
            LOGGER.debug("Created client [{}] with identifier [{}]", client.getName(), client.getKey());
            return List.of(new ConfigurableDelegatedClient(client, qq));
        }
        return List.of();
    }

    /**
     * 构建 微信 Client
     * @param casProperties 配置
     * @return WechatClient
     */
    protected Collection<ConfigurableDelegatedClient> buildWechatProviders(CasConfigurationProperties casProperties) {
        val wechat = extProperties.getWechat();
        if (wechat.isEnabled() && StringUtils.isNotBlank(wechat.getId()) && StringUtils.isNotBlank(wechat.getSecret())) {
            val client = new WechatClient(wechat.getId(), wechat.getSecret());
            client.addScope(wechat.getScope());
            LOGGER.debug("Created client [{}] with identifier [{}]", client.getName(), client.getKey());
            return List.of(new ConfigurableDelegatedClient(client, wechat));
        }
        return List.of();
    }

    /**
     * 构建 企业微信 Client
     * @param casProperties 配置
     * @return WorkWechatClient
     */
    protected Collection<ConfigurableDelegatedClient> buildWorkWechatProviders(CasConfigurationProperties casProperties) {
        val workWechat = extProperties.getWorkWechat();
        if (workWechat.isEnabled() && StringUtils.isNotBlank(workWechat.getId()) && StringUtils.isNotBlank(workWechat.getSecret())) {
            val client = new WorkWechatClient(workWechat.getId(),workWechat.getAgentId(), workWechat.getSecret());

            LOGGER.debug("Created client [{}] with identifier [{}]", client.getName(), client.getKey());
            return List.of(new ConfigurableDelegatedClient(client, workWechat));
        }
        return List.of();
    }

    /**
     * 构建 支付宝 Client
     * @param casProperties 配置
     * @return AlipayClient
     */
    protected Collection<ConfigurableDelegatedClient> buildAlipayProviders(CasConfigurationProperties casProperties) {
        val alipay = extProperties.getAlipay();
        if (alipay.isEnabled() && StringUtils.isNotBlank(alipay.getId()) && StringUtils.isNotBlank(alipay.getSecret())) {
            val client = new AlipayClient(alipay.getId(), alipay.getSecret());
            LOGGER.debug("Created client [{}] with identifier [{}]", client.getName(), client.getKey());
            return List.of(new ConfigurableDelegatedClient(client, alipay));
        }
        return List.of();
    }

    /**
     * 构建 飞书 Client
     * @param casProperties 配置
     * @return FeishuClient
     */
    protected Collection<ConfigurableDelegatedClient> buildWorkFeiShuProviders(CasConfigurationProperties casProperties) {
        val feishu = extProperties.getFeishu();
        if (feishu.isEnabled() && StringUtils.isNotBlank(feishu.getId()) && StringUtils.isNotBlank(feishu.getSecret())) {
            val client = new FeishuClient(feishu.getId(), feishu.getSecret());

            LOGGER.debug("Created client [{}] with identifier [{}]", client.getName(), client.getKey());
            return List.of(new ConfigurableDelegatedClient(client, feishu));
        }
        return List.of();
    }
}
