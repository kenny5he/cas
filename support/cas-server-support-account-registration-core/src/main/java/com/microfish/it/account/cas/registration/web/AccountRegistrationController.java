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

package com.microfish.it.account.cas.registration.web;

import com.microfish.it.account.cas.registration.code.AccountRegistrationCodeManager;
import com.microfish.it.account.cas.registration.code.AccountRegistrationCodeService;
import com.microfish.it.account.cas.registration.persistence.AccountRegistrationConflictException;
import com.microfish.it.account.cas.registration.persistence.AccountRegistrationPersistenceService;
import com.microfish.it.account.cas.registration.web.request.AccountRegistrationCodeRequest;
import com.microfish.it.account.cas.registration.web.request.AccountRegistrationSignUpRequest;
import com.microfish.it.account.cas.registration.web.response.AccountRegistrationTokenResponse;
import com.microfish.it.account.cas.enumate.AccountRegistrationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.acct.AccountRegistrationProperty;
import org.apereo.cas.acct.AccountRegistrationRequest;
import org.apereo.cas.acct.AccountRegistrationService;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.Comparator;
import java.util.Map;

/**
 * Spring MVC endpoints that expose the existing CAS account-registration service
 * to a middle-platform client using JSON {@link ModelAndView} responses.
 *
 * @author kenny
 * @since 7.3.0
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountRegistrationController {
    private final AccountRegistrationService accountRegistrationService;

    private final AccountRegistrationCodeManager accountRegistrationCodeManager;

    private final AccountRegistrationPersistenceService accountRegistrationPersistenceService;

    /**
     * Display the registration page.
     *
     * @param model 页面模型
     * @return 页面访问地址
     */
    @GetMapping("/")
    public String registration(final Model model) {
        var registrationProperties = accountRegistrationService.getAccountRegistrationPropertyLoader()
                .load()
                .values()
                .stream()
                .sorted(Comparator.comparingInt(AccountRegistrationProperty::getOrder))
                .toList();
        model.addAttribute("registrationProperties", registrationProperties);
        return "acc-mgmt/casAccountSignupView";
    }

    /**
     * Validate the requested username.
     *
     * @param properties account properties
     * @return JSON model and view containing the activation token
     */
    @PostMapping(path = "/validate/username", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AccountRegistrationTokenResponse validUsername(@RequestBody final Map<String, Object> properties) {
        val registrationRequest = new AccountRegistrationRequest(properties);
        val username = accountRegistrationService.getAccountRegistrationUsernameBuilder().build(registrationRequest);
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("Account registration username is required");
        }
        if (accountRegistrationPersistenceService.usernameExists(username)) {
            throw new AccountRegistrationConflictException("Account registration username already exists");
        }
        return new AccountRegistrationTokenResponse(username, accountRegistrationService.createToken(registrationRequest));
    }

    /**
     * Send a verification code by email or SMS.
     *
     * @param codeRequest code request
     * @param type registration type
     * @param httpRequest current HTTP request
     */
    @PostMapping(path = "/{type}/code", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void getCodeByRegistrationType(@PathVariable final String type,
                                          @RequestBody final AccountRegistrationCodeRequest codeRequest,
                                          final HttpServletRequest httpRequest) {
        val registrationRequest = new AccountRegistrationRequest(codeRequest.getProperties());
        val registrationType = AccountRegistrationType.getByCode(type);
        assertContactAvailable(registrationRequest, registrationType);
        accountRegistrationCodeManager.createCode(registrationRequest, registrationType, httpRequest);
    }

    /**
     * Create an account.
     *    1. 校验参数，获取配置的必填字段、校验规则等，逐一解析参数校验
     *    2. 校验Code
     *    3. 创建账号，保存在数据库中
     *    4. 将新注册的账户信息自动配置到指定的外部系统或目标平台中
     * @param signUpRequest signup
     * @return model and view
     */
    @PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String register(@Valid @RequestBody final AccountRegistrationSignUpRequest signUpRequest) throws Throwable {
        val registrationRequest = new AccountRegistrationRequest(signUpRequest.getProperties());
        val registrationType = AccountRegistrationType.getByCode(signUpRequest.getRegistrationType());
        accountRegistrationService.getAccountRegistrationRequestValidator().validate(registrationRequest);
        val username = accountRegistrationService.getAccountRegistrationUsernameBuilder().build(registrationRequest);
        assertAccountAvailable(registrationRequest, username, registrationType);
        registrationRequest.putProperty(AccountRegistrationCodeService.PROPERTY_VERIFICATION_CODE, signUpRequest.getCode());
        accountRegistrationCodeManager.validateCode(registrationRequest, registrationType);
        accountRegistrationPersistenceService.save(registrationRequest, username);
        accountRegistrationService.getAccountRegistrationProvisioner().provision(registrationRequest);
        return "/login/casAccountSignupViewCompleted";
    }

    private void assertAccountAvailable(final AccountRegistrationRequest registrationRequest,
                                        final String username,
                                        final AccountRegistrationType registrationType) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("Account registration username is required");
        }
        if (accountRegistrationPersistenceService.usernameExists(username)) {
            throw new AccountRegistrationConflictException("Account registration username already exists");
        }
        assertContactAvailable(registrationRequest, registrationType);
    }

    private void assertContactAvailable(final AccountRegistrationRequest registrationRequest,
                                        final AccountRegistrationType registrationType) {
        if (registrationType == AccountRegistrationType.EMAIL
            && StringUtils.isNotBlank(registrationRequest.getEmail())
            && accountRegistrationPersistenceService.emailExists(registrationRequest.getEmail())) {
            throw new AccountRegistrationConflictException("Account registration email address already exists");
        }
        if (registrationType == AccountRegistrationType.PHONE
            && StringUtils.isNotBlank(registrationRequest.getPhone())
            && accountRegistrationPersistenceService.phoneExists(registrationRequest.getPhone())) {
            throw new AccountRegistrationConflictException("Account registration phone number already exists");
        }
    }
}
