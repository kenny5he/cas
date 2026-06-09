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
 * 选项
 */
drop table if exists `cas_selection`;
CREATE TABLE `cas_selection` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Selection ID，非空',
    `name` varchar(255) NOT NULL COMMENT '选项名称，非空',
    `label` varchar(255) NULL COMMENT '选项标签，非空',
    `title` varchar(255) NULL COMMENT '选项标题，非空',
    `pattern` varchar(255) NULL COMMENT '表达式，非空',
    `type` varchar(255) NOT NULL COMMENT '类型，非空',
    `required` int(1) NOT NULL DEFAULT 0 '是否必填',
    `disabled` int(1) NOT NULL DEFAULT 1 '是否禁用',
    PRIMARY KEY (`id`)
);
/**
 * 选项值
 */
drop table if exists `cas_selection_item`;
CREATE TABLE `cas_selection_item` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Item ID，非空',
    `selection_id` bigint NOT NULL COMMENT 'Selection ID，非空',
    `name` varchar(255) NOT NULL COMMENT '值名称，非空',
    `value` varchar(255) NOT NULL COMMENT '值，非空',
    PRIMARY KEY (`id`)
);
/**
 * 租户选项
 */
drop table if exists `cas_selection_tenant`;
CREATE TABLE `cas_selection_tag` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Tag ID，非空',
    `selection_id` bigint NOT NULL COMMENT '选项 ID，非空',
    `tenant` varchar(255) NOT NULL COMMENT '租户，非空',
    PRIMARY KEY (`id`)
);
```
3. 创建注册选项数据库实体类
```java

```