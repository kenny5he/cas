package com.microfoolish.it.login.cas.pac4j.handler.support;

import com.microfoolish.it.login.cas.pac4j.client.WorkWechatClient;
import org.apereo.cas.support.pac4j.authentication.clients.DelegatedClientFactoryCustomizer;
import org.pac4j.core.client.Client;

public class WorkWechatDelegatedClientFactory implements DelegatedClientFactoryCustomizer<WorkWechatClient> {
    @Override
    public int getOrder() {
        return DelegatedClientFactoryCustomizer.super.getOrder();
    }

    @Override
    public void customize(WorkWechatClient client) {

    }
}
