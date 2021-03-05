
package org.apereo.cas;

import org.apereo.cas.pm.web.flow.PasswordManagementSingleSignOnParticipationStrategyTests;
import org.apereo.cas.pm.web.flow.PasswordManagementWebflowConfigurerDisabledTests;
import org.apereo.cas.pm.web.flow.PasswordManagementWebflowConfigurerEnabledTests;
import org.apereo.cas.pm.web.flow.actions.HandlePasswordExpirationWarningMessagesActionTests;
import org.apereo.cas.pm.web.flow.actions.InitPasswordResetActionTests;
import org.apereo.cas.pm.web.flow.actions.SendPasswordResetInstructionsActionTests;
import org.apereo.cas.pm.web.flow.actions.ValidatePasswordResetTokenActionTests;
import org.apereo.cas.pm.web.flow.actions.VerifyPasswordResetRequestActionTests;
import org.apereo.cas.pm.web.flow.actions.VerifySecurityQuestionsActionTests;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

/**
 * This is {@link AllTestsSuite}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0-RC3
 */
@SelectClasses({
    VerifySecurityQuestionsActionTests.class,
    SendPasswordResetInstructionsActionTests.class,
    InitPasswordResetActionTests.class,
    PasswordManagementSingleSignOnParticipationStrategyTests.class,
    PasswordManagementWebflowConfigurerEnabledTests.class,
    PasswordManagementWebflowConfigurerDisabledTests.class,
    ValidatePasswordResetTokenActionTests.class,
    VerifyPasswordResetRequestActionTests.class,
    HandlePasswordExpirationWarningMessagesActionTests.class
})
@RunWith(JUnitPlatform.class)
public class AllTestsSuite {
}
