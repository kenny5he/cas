package org.apereo.cas.acct.provision;

import org.apereo.cas.acct.AccountRegistrationRequest;
import org.apereo.cas.acct.AccountRegistrationResponse;

/**
 * This is {@link AccountRegistrationProvisioner}.
 * 账号注册后置处理接口
 *
 * CAS 的“注册完成后的账号分发/同步扩展点”，
 * 作用是把新注册用户的账户信息真正“Provision（配置/同步）”到外部身份管理系统中。
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
