# Cas Gradle
## Cas Gradle Change Info
1. jdbc
    1. cas-server-support-jdbc 项目中添加 依赖包
    ```
    implementation libs.postgresql
    ```
2. jpa
    1. libs.version.toml文件 添加spring-boot-starter-data-jpa依赖
    ```
    spring-boot-starter-data-jpa = { module = "org.springframework.boot:spring-boot-starter-data-jpa", version.ref = "springBoot"}
    ```
3. authentication
    
4. Webapp
    1. 依赖包 webapp-dependencies.gradle
    
## Extension Module
1. Themes
    1. cas-server-support-themes-extension
2. Account Management
    1. cas-server-support-account-mgmt-extension
    2. cas-server-support-account-mgmt-jpa
3. JPA Authentication
    1. cas-server-support-jpa-authentication
4. Pac4j Authentication
    1. cas-server-support-pac4j-extension
5. OIDC 
6. Oauth
7. Cookie
8. Session