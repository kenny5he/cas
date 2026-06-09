# 用户注册
https://apereo.github.io/cas/development/registration/Account-Registration-Overview.html

## 添加jar包依赖
```
implementation project(":support:cas-server-support-account-mgmt")
```

## WebPage 注册页面
1. 注册页面
   - templates/acct-mgmt/casAccountSignupView.html
2. 默认注册页选项配置
   - 文件: account-registration-properties/registration-properties.json
3. 页面加载bean: CasWebflowConstants.ACTION_ID_LOAD_ACCOUNT_REGISTRATION_PROPERTIES
4. 页面加载处理类: org.apereo.cas.acct.webflow.LoadAccountRegistrationPropertiesAction
5. 选项属性类: org.apereo.cas.acct.AccountRegistrationProperty

### Configuration
1. 自动配置类: CasAccountManagementWebflowConfiguration

### WebFlow
1. 注册信息填写(SubmitAccountRegistrationAction)
   
2. 验证注册Token(ValidateAccountRegistrationTokenAction)

3. 填写密码、密保问题完成注册(FinalizeAccountRegistrationAction)