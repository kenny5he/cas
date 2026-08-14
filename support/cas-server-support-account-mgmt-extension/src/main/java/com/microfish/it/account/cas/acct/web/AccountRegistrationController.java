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

import org.apereo.cas.acct.AccountRegistrationProperty;
import org.apereo.cas.acct.AccountRegistrationRequest;
import org.apereo.cas.acct.AccountRegistrationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Spring MVC endpoints that expose the existing CAS account-registration service
 * to a middle-platform client using JSON {@link ModelAndView} responses.
 *
 * @author kenny
 * @since 7.3.0
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "register", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountRegistrationController {
    private static final String MODEL_ATTRIBUTE_SUCCESS = "success";

    private static final MappingJackson2JsonView JSON_VIEW = new MappingJackson2JsonView();

    private final AccountRegistrationService accountRegistrationService;

    /**
     * Return the configured registration fields in display order.
     *
     * @return JSON model and view
     */
    @GetMapping("/")
    public ModelAndView getRegistrationProperties() {
        val properties = accountRegistrationService.getAccountRegistrationPropertyLoader()
            .load()
            .values()
            .stream()
            .sorted(Comparator.comparingInt(AccountRegistrationProperty::getOrder))
            .toList();
        return json(HttpStatus.OK, Map.of(
            MODEL_ATTRIBUTE_SUCCESS, Boolean.TRUE,
            "registrationProperties", properties));
    }

    /**
     * Directly provision an account. This endpoint is intended for a trusted
     * middle-platform service that has already verified the registrant.
     *
     * @param properties account properties
     * @return JSON model and view
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ModelAndView register(@RequestBody final Map<String, Object> properties) {
        return provision(buildRegistrationRequest(properties));
    }

    /**
     * Validate initial account properties and create an activation token.
     *
     * @param properties account properties
     * @return JSON model and view containing the activation token
     */
    @PostMapping(path = "/token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ModelAndView createRegistrationToken(@RequestBody final Map<String, Object> properties) {
        val registrationRequest = buildRegistrationRequest(properties);
        val username = accountRegistrationService.getAccountRegistrationUsernameBuilder().build(registrationRequest);
        val token = accountRegistrationService.createToken(registrationRequest);
        return json(HttpStatus.CREATED, Map.of(
            MODEL_ATTRIBUTE_SUCCESS, Boolean.TRUE,
            "username", Objects.toString(username, StringUtils.EMPTY),
            "token", token));
    }

    /**
     * Validate an activation token, merge final account properties such as a
     * password, and provision the account.
     *
     * @param activationRequest activation request
     * @return JSON model and view
     */
    @PostMapping(path = "/activate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ModelAndView activate(@RequestBody final AccountRegistrationActivationRequest activationRequest) {
        if (activationRequest == null || StringUtils.isBlank(activationRequest.getToken())) {
            throw new IllegalArgumentException("Registration token is required");
        }
        val registrationRequest = accountRegistrationService.validateToken(activationRequest.getToken());
        if (registrationRequest == null) {
            throw new IllegalArgumentException("Registration token is invalid or has expired");
        }
        registrationRequest.putProperties(activationRequest.getProperties());
        accountRegistrationService.getAccountRegistrationRequestValidator().validate(registrationRequest);
        return provision(registrationRequest);
    }

    /**
     * Return invalid registration requests as JSON instead of an HTML error page.
     *
     * @param exception request failure
     * @return JSON model and view
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleInvalidRequest(final IllegalArgumentException exception) {
        LOGGER.debug("Invalid account registration request", exception);
        return json(HttpStatus.BAD_REQUEST, Map.of(
            MODEL_ATTRIBUTE_SUCCESS, Boolean.FALSE,
            "error", "invalid_request",
            "message", Objects.toString(exception.getMessage(), "Invalid request")));
    }

    private AccountRegistrationRequest buildRegistrationRequest(final Map<String, Object> properties) {
        if (properties == null || properties.isEmpty()) {
            throw new IllegalArgumentException("Registration properties are required");
        }
        val configuredProperties = accountRegistrationService.getAccountRegistrationPropertyLoader().load().values();
        configuredProperties.forEach(property -> validateProperty(property, properties.get(property.getName())));

        val registrationRequest = new AccountRegistrationRequest(properties);
        accountRegistrationService.getAccountRegistrationRequestValidator().validate(registrationRequest);
        return registrationRequest;
    }

    private ModelAndView provision(final AccountRegistrationRequest registrationRequest) throws Throwable {
        val response = accountRegistrationService.getAccountRegistrationProvisioner().provision(registrationRequest);

        val model = new LinkedHashMap<String, Object>(response.getProperties());
        model.putIfAbsent(MODEL_ATTRIBUTE_SUCCESS, response.isSuccess());
        val username = accountRegistrationService.getAccountRegistrationUsernameBuilder().build(registrationRequest);
        if (StringUtils.isNotBlank(username)) {
            model.putIfAbsent("username", username);
        }
        return json(response.isSuccess() ? HttpStatus.CREATED : HttpStatus.UNPROCESSABLE_ENTITY, model);
    }

    private static void validateProperty(final AccountRegistrationProperty property, final Object value) {
        val text = Objects.toString(value, StringUtils.EMPTY);
        if (property.isRequired() && StringUtils.isBlank(text)) {
            throw new IllegalArgumentException("Required registration property is missing: " + property.getName());
        }
        if (StringUtils.isNotBlank(text)
            && !"select".equalsIgnoreCase(property.getType())
            && StringUtils.isNotBlank(property.getPattern())
            && !Pattern.matches(property.getPattern(), text)) {
            throw new IllegalArgumentException("Registration property has an invalid value: " + property.getName());
        }
    }

    private static ModelAndView json(final HttpStatus status, final Map<String, ?> model) {
        val modelAndView = new ModelAndView(JSON_VIEW, new LinkedHashMap<>(model));
        modelAndView.setStatus(status);
        return modelAndView;
    }
}
