# Jdbc 认证信息
## 通过Jdbc查询认证信息
### 配置式 查询认证
1. 配置文件类: AuthenticationProperties
2. 配置加载类: CasJdbcAuthenticationConfiguration

| 配置属性                  | bean名 | 处理类 | 作用 |
|-----------------------|-------|----|----|
| cas.authn.jdbc.query  | queryDatabaseAuthenticationHandlers | QueryDatabaseAuthenticationHandler |    | 
| cas.authn.jdbc.encode | queryAndEncodeDatabaseAuthenticationHandlers | QueryAndEncodeDatabasePasswordEncoder |    | 
| cas.authn.jdbc.search | searchModeSearchDatabaseAuthenticationHandlers | SearchModeSearchDatabaseAuthenticationHandler |    | 
| cas.authn.jdbc.bind   | bindModeSearchDatabaseAuthenticationHandlers | BindModeSearchDatabaseAuthenticationHandler |    | 

### 客制化 查询认证 SQL
1. 新建模块
   1. cas-server-support-jpa-authentication
   2. grade 文件: settings.gradle
      ```
      include "support:cas-server-support-jpa-authentication"
      ```
2. 用户名认证查询处理类
   1. UserNameQueryAuthenticationHandler
3. 邮件认证查询处理类(由于规则不一样，所以查询匹配不一致)
   1. MailQueryAuthenticationHandler
4. 手机号码认证查询类(UnSupport: 不支持，手机方式通过验证码校验，而非密码校验)

## 代理认证 Jdbc (cas-server-support-surrogate)
