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

package com.microfish.it.account.cas.acct.web;

import com.microfish.it.account.cas.enumate.AccountRegistrationType;
import org.apereo.cas.acct.AccountRegistrationProperty;
import org.apereo.cas.acct.AccountRegistrationRequest;
import org.apereo.cas.acct.AccountRegistrationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Comparator;
import java.util.Map;

import com.microfish.it.account.cas.acct.web.request.AccountRegistrationCodeRequest;
import com.microfish.it.account.cas.acct.web.request.AccountRegistrationSignUpRequest;
import com.microfish.it.account.cas.acct.web.response.AccountRegistrationTokenResponse;

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
    @Autowired
    private final AccountRegistrationService accountRegistrationService;

    /**
     * 注册页面
     * @param model 页面模型
     * @return 页面访问地址
     */
    @GetMapping("/")
    public String registration(Model model) {
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
     * 校验用户名
     *
     * @param properties account properties
     * @return JSON model and view containing the activation token
     */
    @PostMapping(path = "/validate/username", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AccountRegistrationTokenResponse validUsername(@RequestBody final Map<String, Object> properties) {
        val registrationRequest = new AccountRegistrationRequest(properties);
        val username = accountRegistrationService.getAccountRegistrationUsernameBuilder().build(registrationRequest);
        return accountRegistrationService.validateUsername(username);;
    }

    /**
     * 给邮箱/手机发送验证码
     *
     * @param codeRequest code request
     * @return JSON model and view
     */
    @PostMapping(path = "/{type}/code", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void getCodeByRegistrationType(@RequestBody final AccountRegistrationCodeRequest codeRequest) throws Exception {
        val registrationRequest = new AccountRegistrationRequest(codeRequest.getProperties());
        accountRegistrationService.createCode(registrationRequest, AccountRegistrationType.valueOf(codeRequest.getRegistrationType()));
    }

    /**
     * 创建账号
     *    1. 校验参数，获取配置的必填字段、校验规则等，逐一解析参数校验
     *    2. 校验Code
     *    3. 创建账号，保存在数据库中
     *    4. 将新注册的账户信息自动配置到指定的外部系统或目标平台中
     * @param signUpRequest signup
     * @return model and view
     */
    @PostMapping(path = "/")
    public String register(@RequestBody final AccountRegistrationSignUpRequest signUpRequest) throws Throwable {
        var registrationRequest = new AccountRegistrationRequest(signUpRequest.getProperties());
        accountRegistrationService.getAccountRegistrationRequestValidator().validate(registrationRequest);
        accountRegistrationService.validateCode(registrationRequest, AccountRegistrationType.valueOf(signUpRequest.getRegistrationType()));
        accountRegistrationService.createAccount(registrationRequest);
        accountRegistrationService.getAccountRegistrationProvisioner().provision(registrationRequest);
        return "/login/casAccountSignupViewCompleted";
    }
}
