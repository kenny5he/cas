/** 1.1.1 创建属性表  **/
CREATE TABLE IF NOT EXISTS cas_property_t(
    property_id int8 NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    label VARCHAR(50)  NOT NULL,
    title VARCHAR(100)  NOT NULL,
    "pattern" VARCHAR(50)  NOT NULL,
    category VARCHAR(20)  NOT NULL,
    "type" VARCHAR(20)  NOT NULL,
    required int8 NOT NULL default 0,
    disabled int8 NOT NULL default 0,
    "css_class" VARCHAR(20)  NOT NULL,
    "valid_message" VARCHAR(20) NOT NULL,
    tenant_id int8  NOT NULL,
    creation_date timestamp NOT NULL ,
    created_by VARCHAR(10),
    last_update_date timestamp NULL,
    last_update_by VARCHAR(10) NULL,
    PRIMARY KEY(property_id)
);
/** 1.1.2 属性表注释 **/
COMMENT ON TABLE cas_property_t is '属性表';
COMMENT ON COLUMN cas_property_t.property_id is '主键，属性ID，无符号、非空。';
COMMENT ON COLUMN cas_property_t.name is '属性名';
COMMENT ON COLUMN cas_property_t.label is '属性标签';
COMMENT ON COLUMN cas_property_t.title is '标题，非空';
COMMENT ON COLUMN cas_property_t.pattern is '匹配';
COMMENT ON COLUMN cas_property_t.category is '类别：邮箱注册/手机注册';
COMMENT ON COLUMN cas_property_t.type is '类型';
COMMENT ON COLUMN cas_property_t.required is '是否必填';
COMMENT ON COLUMN cas_property_t.disabled is '是否禁用';
COMMENT ON COLUMN cas_property_t.css_class is '样式';
COMMENT ON COLUMN cas_property_t.valid_message is '校验提示信息';
COMMENT ON COLUMN cas_property_t.tenant_id is '租户Id';
COMMENT ON COLUMN cas_property_t.creation_date is '创建时间';
COMMENT ON COLUMN cas_property_t.created_by is '创建人';
COMMENT ON COLUMN cas_property_t.last_update_date is '最后更新时间';
COMMENT ON COLUMN cas_property_t.last_update_by is '最后更新人';

/** 1.1.3 创建序列 **/
CREATE SEQUENCE "cas_property_id_s" START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 1;
/** 1.1.4 创建索引 **/

/** 1.2.1 创建属性值表  **/
CREATE TABLE IF NOT EXISTS cas_property_value_t(
    id int8 NOT NULL,
    property_id int8 NOT NULL,
    "code" VARCHAR(50) NOT NULL,
    "value" VARCHAR(200)  NOT NULL,
    enabled int8 NOT NULL default 1,
    creation_date timestamp NOT NULL,
    created_by VARCHAR(10),
    last_update_date timestamp NULL,
    last_update_by VARCHAR(10) NULL,
    PRIMARY KEY(id)
);
/** 1.2.2 属性表注释 **/
COMMENT ON TABLE cas_property_value_t is '属性值表';
COMMENT ON COLUMN cas_property_value_t.property_id is '属性表id，无符号、非空。';
COMMENT ON COLUMN cas_property_value_t.code is '属性编码';
COMMENT ON COLUMN cas_property_value_t.value is '属性值';
COMMENT ON COLUMN cas_property_value_t.enabled is '是否启用';
COMMENT ON COLUMN cas_property_t.creation_date is '创建时间';
COMMENT ON COLUMN cas_property_t.created_by is '创建人';
COMMENT ON COLUMN cas_property_t.last_update_date is '最后更新时间';
COMMENT ON COLUMN cas_property_t.last_update_by is '最后更新人';
/** 1.2.3 创建序列 **/
CREATE SEQUENCE "cas_property_value_id_s" START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 1;
/** 1.2.4 创建索引 **/

/** 2.1.1 创建用户表 **/
CREATE TABLE IF NOT EXISTS cas_user_t(
    user_id int8 NOT NULL,
    user_account VARCHAR(20) NOT NULL,
    user_name VARCHAR(60) NOT NULL,
    nike_name VARCHAR(60)  NOT NULL,
    first_name varchar(60) NOT NULL,
    last_name varchar(60) NOT NULL,
    gender integer NULL,
    password varchar(50) NULL,
    expired integer NOT NULL,
    mobile_phone VARCHAR(20) NULL,
    email VARCHAR(50) NULL,
    "type" VARCHAR(5) NOT NULL,
    enabled integer NOT NULL default 1,
    start_date timestamp NOT NULL,
    end_date timestamp NULL,
    tenant_id int8  NOT NULL,
    tenant_code VARCHAR(50) NOT NULL,
    creation_date timestamp NOT NULL ,
    created_by VARCHAR(10),
    last_update_date timestamp NULL ,
    last_update_by VARCHAR(10) NULL ,
    PRIMARY KEY(user_id)
);

/** 2.1.2 用户表字段添加注释 **/
COMMENT ON TABLE cas_user_t is '用户表';
COMMENT ON COLUMN cas_user_t.user_id is '主键，用户ID，无符号、非空。';
COMMENT ON COLUMN cas_user_t.user_account is '用户账号';
COMMENT ON COLUMN cas_user_t.user_name is '用户名称，非空';
COMMENT ON COLUMN cas_user_t.nike_name is '昵称，非空';
COMMENT ON COLUMN cas_user_t.mobile_phone is '手机号码';
COMMENT ON COLUMN cas_user_t.email is 'email 邮箱';
COMMENT ON COLUMN cas_user_t.type is '用户类型/是否为虚拟用户';
COMMENT ON COLUMN cas_user_t.enabled is '用户状态 失效 0, 有效 1';
COMMENT ON COLUMN cas_user_t.start_date is '生效日期';
COMMENT ON COLUMN cas_user_t.end_date is '失效日期';
COMMENT ON COLUMN cas_user_t.creation_date is '创建时间';
COMMENT ON COLUMN cas_user_t.created_by is '记录创建人';
COMMENT ON COLUMN cas_user_t.last_update_date is '最后一次更改日期';
COMMENT ON COLUMN cas_user_t.last_update_by is '最后一次更改人';

/** 2.2.3 创建序列 **/
CREATE SEQUENCE "cas_user_id_s" START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 1;
/** 2.2.4 创建索引 **/
ALTER TABLE cas_user_t ADD CONSTRAINT unique_user_account UNIQUE (user_account);
