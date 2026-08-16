package org.apereo.cas.acct.provision;

import org.apereo.cas.acct.AccountRegistrationRequest;
import org.apereo.cas.acct.AccountRegistrationResponse;

/**
 * 处理账户的自助注册请求，并将新注册的账户信息自动配置到指定的外部系统或目标平台中
 *
 * This is {@link AccountRegistrationProvisioner}.
 *
 * @author Misagh Moayyed
 * @since 6.5.0
 */
@FunctionalInterface
public interface AccountRegistrationProvisioner {
    /**
     * Default bean name.
     */
    String BEAN_NAME = "accountMgmtRegistrationProvisioner";

    /**
     * Provision.
     *
     * @param request the request
     * @return the account registration response
     * @throws Throwable the throwable
     */
    AccountRegistrationResponse provision(AccountRegistrationRequest request) throws Throwable;
}
