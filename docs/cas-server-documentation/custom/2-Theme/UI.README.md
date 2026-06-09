# Login UI (cas-server-support-thymeleaf)
1. 官方文档: https://apereo.github.io/cas/7.0.x/ux/User-Interface-Customization-Themes-Static.html
2. 默认登录页面 (templates/login/casLoginView.html)

## 添加模板引擎依赖
1. 添加依赖包
```
implementation project(":support:cas-server-support-thymeleaf")
```

## Custom UI CSS
1. 设置默认主题
```properties (ThemeProperties)
cas.theme.default-theme-name=microfoolish
cas.theme.name=microfoolish
cas.theme.param-name=theme
```
2. 设定 css.file
    - 配置读取类由 DefaultCasThemeSource 或 AggregateCasThemeSource 读取，通过父类 ResourceBundleThemeSource 的 getTheme方法加载其配置信息。
    ```properties 
    cas.theme.name=Microfoolish Theme
    cas.theme.description=Microfoolish - Central Authentication Service
    
    cas.standard.css.file=/themes/microfoolish/css/cas.css
    cas.standard.js.file=/themes/microfoolish/js/cas.js
    cas.logo.file=/themes/microfoolish/images/logo.svg
    cas.favicon.file=/themes/microfoolish/images/favicon.ico
    
    cas.drawer-menu.enabled=true
    cas.notifications-menu.enabled=false
    cas.login-form.enabled=true
    cas.pm-links.enabled=true
    cas.public-workstation.enabled=false
    cas.warn-on-redirect.enabled=false
    ```
3. 创建登录页面配置表
```sql
/**
 * 主题配置
 */
drop table if exists `cas_theme_setting`;
CREATE TABLE `cas_theme_setting` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Selection ID，非空',
    `name` varchar(255) NOT NULL COMMENT '主题名，非空',
    `key` varchar(255) NOT NULL COMMENT '属性名，非空',
    `value` varchar(255) NOT NULL COMMENT '属性默认值，非空',
    PRIMARY KEY (`id`)
);
/**
 * 租户主题
 */
drop table if exists `cas_theme_tenant`;
CREATE TABLE `cas_theme_tenant` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Selection ID，非空',
    `tenant` varchar(255) NOT NULL COMMENT '租户',
    `name` varchar(255) NOT NULL COMMENT '主题名，非空',
    `key` varchar(255) NOT NULL COMMENT '属性名，非空',
    `value` varchar(255) NOT NULL COMMENT '属性默认值，非空',
    PRIMARY KEY (`id`)
);
```
4. 创建注册选项数据库实体类
```

```