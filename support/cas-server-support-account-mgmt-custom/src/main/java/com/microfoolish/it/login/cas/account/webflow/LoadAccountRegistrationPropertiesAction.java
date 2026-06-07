package com.microfoolish.it.login.cas.account.webflow;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apereo.cas.acct.AccountRegistrationProperty;
import org.apereo.cas.web.flow.actions.BaseCasWebflowAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.util.Comparator;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LoadAccountRegistrationPropertiesAction extends BaseCasWebflowAction {



    @Override
    protected Event doExecuteInternal(RequestContext requestContext) throws Throwable {
//        val properties = accountRegistrationService.getAccountRegistrationPropertyLoader().load();
//        requestContext.getFlowScope().put("registrationProperties", properties
//                .values()
//                .stream()
//                .sorted(Comparator.comparing(AccountRegistrationProperty::getOrder))
//                .collect(Collectors.toList()));
        return null;
    }
}
