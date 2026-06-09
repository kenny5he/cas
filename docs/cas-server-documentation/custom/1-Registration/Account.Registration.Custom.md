## 客制化 注册
1. 新建模块
    1. cas-server-support-account-mgmt-jdbc
    2. grade 文件: settings.gradle
       ```
       include "support:cas-server-support-account-mgmt-jdbc"
       ```
2. 创建 注册选项 表信息
```sql
/**
 * 字段
 */
drop table if exists `cas_property_t`;
CREATE TABLE `cas_property_t` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Selection ID，非空',
    `name` varchar(50) NOT NULL COMMENT '选项名称，非空',
    `label` varchar(50) NULL COMMENT '选项标签，非空',
    `title` varchar(100) NULL COMMENT '选项标题，非空',
    `pattern` varchar(50) NULL COMMENT '表达式，非空',
    `category` varchar(20) NOT NULL COMMENT '类别，非空（注册等场景字段）',
    `type` varchar(20) NOT NULL COMMENT '类型，非空',
    `required` int(1) NOT NULL DEFAULT 0 COMMENT '是否必填',
    `disabled` int(1) NOT NULL DEFAULT 1 COMMENT '是否禁用',
    `css_class` varchar(100) NULL COMMENT 'css样式',
    `valid_message` varchar(200) DEFAULT 'cas.screen.acct.error.invalid-value'
    `tenant_id` bigint NULL COMMENT '租户Id',
    `creation_date` timestamp NOT NULL COMMENT '创建时间',
    `created_by` VARCHAR(10) NOT NULL COMMENT '创建人',
    `last_update_date` timestamp NULL COMMENT '更新时间',
    `last_update_by` VARCHAR(10) NULL COMMENT '更信人',
    PRIMARY KEY (`id`)
);
/**
 * 字段值
 */
drop table if exists `cas_property_value_t`;
CREATE TABLE `cas_property_value_t` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '值 ID，非空',
    `property_id` bigint NOT NULL COMMENT '属性 ID，非空',
    `code` varchar(255) NOT NULL COMMENT '编码，非空',
    `value` varchar(255) NOT NULL COMMENT '值，非空',
    `creation_date` timestamp NOT NULL COMMENT '创建时间',
    `created_by` VARCHAR(10) NOT NULL COMMENT '创建人',
    `last_update_date` timestamp NULL COMMENT '更新时间',
    `last_update_by` VARCHAR(10) NULL COMMENT '更信人',
    PRIMARY KEY (`id`)
);
```
3. 创建注册选项数据库实体类
```java

```