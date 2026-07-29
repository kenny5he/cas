# 用户注册
https://apereo.github.io/cas/development/registration/Account-Registration-Overview.html

## 添加jar包依赖
```
implementation project(":support:cas-server-support-account-mgmt")
```

## 核心实现类
1. 账号注册实现：DefaultAccountRegistrationService
2. 账号注册属性配置加载：DefaultAccountRegistrationService
3. 

## WebPage 注册页面
1. 注册页面
   - templates/acct-mgmt/casAccountSignupView.html
2. 默认注册页选项配置
   - 文件: account-registration-properties/registration-properties.json
3. 页面加载bean: CasWebflowConstants.ACTION_ID_LOAD_ACCOUNT_REGISTRATION_PROPERTIES
4. 页面加载处理类: org.apereo.cas.acct.webflow.LoadAccountRegistrationPropertiesAction
5. 选项属性类: org.apereo.cas.acct.AccountRegistrationProperty

### Configuration
1. 自动配置类: CasAccountManagementWebflowAutoConfiguration

### WebFlow
1. 修改注册地址 cas/register
   1. 账号管理Webflow配置类：AccountManagementWebflowConfigurer
2. 注册信息填写(SubmitAccountRegistrationAction)
   
3. 验证注册Token(ValidateAccountRegistrationTokenAction)

4. 填写密码、密保问题完成注册(FinalizeAccountRegistrationAction)