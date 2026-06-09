package com.microfish.it.login.cas.pac4j.support;

import com.microfish.it.login.cas.pac4j.client.WorkWechatClient;
import org.apereo.cas.support.pac4j.authentication.clients.DelegatedClientFactoryCustomizer;

public class WorkWechatDelegatedClientFactory implements DelegatedClientFactoryCustomizer<WorkWechatClient> {
    @Override
    public int getOrder() {
        return DelegatedClientFactoryCustomizer.super.getOrder();
    }

    @Override
    public void customize(WorkWechatClient client) {

    }
}
