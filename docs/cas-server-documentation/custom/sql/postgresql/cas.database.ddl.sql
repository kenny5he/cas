/** 0. containerd: nerdctl compose -f docker-compose.yml up & **/
/** 1. 新建一个独立用户，并授予角色，参考文档: http://www.postgres.cn/docs/9.3/sql-createuser.html **/
/** 1.1 新建一个 默认的角色 配置，默认连接数 200 **/
CREATE ROLE default_role WITH NOSUPERUSER NOCREATEDB NOCREATEROLE INHERIT CONNECTION LIMIT 200;

/** 1.2 新建一个 account 用户 **/
CREATE USER account WITH ENCRYPTED PASSWORD 'account-pg$123' IN ROLE default_role;

/** 2. 新建一个表空间，参考文档: http://www.postgres.cn/docs/9.3/sql-createtablespace.html **/
/**  2.1 docker/containerd 需在container目录中存在该路径： **/
/**   2.1.1 创建目录  **/
/**    1） nerdctl exec -it account sh **/
/**    2） mkdir -p /var/lib/postgresql/data/account **/
/**    3） chown postgres:postgres /var/lib/postgresql/data/account **/
CREATE TABLESPACE account OWNER account LOCATION '/var/lib/postgresql/data/account';

/** 3. 新建数据库, 参考文档:  **/
/** 3.1 字符集信息, 参考文档: http://www.postgres.cn/docs/9.3/multibyte.html#MULTIBYTE-CHARSET-SUPPORTED **/
CREATE DATABASE account WITH OWNER account ENCODING 'UTF8' CONNECTION LIMIT 500 TABLESPACE account;
/** 3.1.1 调整数据库 Owner 给指定用户  **/
ALTER DATABASE account OWNER TO account;
/** 3.2 授予角色连接数据库的权限 (Owner 无需执行) **/
GRANT CONNECT ON DATABASE account TO account;
/** 3.3 授予创建权限 (Owner 无需执行) **/
GRANT CREATE ON DATABASE account TO account;

/** 4.2 创建 Schema: account **/
CREATE SCHEMA IF NOT EXISTS account AUTHORIZATION account;

/** 参考自: https://tableplus.com/blog/2018/04/postgresql-how-to-create-read-only-user.html **/
/** 5 新建一个 查询 GLOBALQUERY 用户 **/
CREATE USER globalquery WITH PASSWORD 'account$query';
/** 5.1 授予角色连接数据库的权限 **/
GRANT CONNECT ON DATABASE account TO globalquery;
/** 5.2 授予角色连接Schema的权限 (切换到 account 账号执行) **/
GRANT USAGE ON SCHEMA account TO globalquery;
/** 5.3 授予用户表查询权限 **/
/** GRANT SELECT ON cs_user_t TO globalquery; **/
/** 5.4 授予Schema下所有表查询权限 **/
GRANT SELECT ON ALL TABLES IN SCHEMA account TO globalquery;
/** 5.5 授予新建表查询权限 **/
ALTER DEFAULT PRIVILEGES IN SCHEMA account GRANT SELECT ON TABLES TO globalquery;
