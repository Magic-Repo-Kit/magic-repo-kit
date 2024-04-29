/*
 Navicat Premium Data Transfer

 Source Server         : 182
 Source Server Type    : MySQL
 Source Server Version : 50744 (5.7.44)
 Source Host           : 182.92.129.14:3306
 Source Schema         : gpt

 Target Server Type    : MySQL
 Target Server Version : 50744 (5.7.44)
 File Encoding         : 65001

 Date: 29/04/2024 11:47:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gpt_conversation
-- ----------------------------
DROP TABLE IF EXISTS `gpt_conversation`;
CREATE TABLE `gpt_conversation` (
  `id` bigint(20) NOT NULL COMMENT '主键id',
  `conversation_id` varchar(255) DEFAULT NULL COMMENT '会话id',
  `title` varchar(255) DEFAULT NULL COMMENT '会话标题',
  `gpt_role_id` bigint(20) DEFAULT NULL COMMENT 'gpt角色id',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_flag` int(11) DEFAULT NULL COMMENT '状态[0:未删除,1:删除]',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='gpt会话表';

-- ----------------------------
-- Records of gpt_conversation
-- ----------------------------
BEGIN;
INSERT INTO `gpt_conversation` (`id`, `conversation_id`, `title`, `gpt_role_id`, `create_user`, `create_time`, `update_user`, `update_time`, `delete_flag`) VALUES (1784791280748896258, 'c995c7df-7437-4763-9870-5c832a9e6c53', '\n我是一个人工智能语', 3, 1, '2024-04-29 11:46:16', 1, '2024-04-29 11:46:16', 0);
COMMIT;

-- ----------------------------
-- Table structure for gpt_conversation_detail
-- ----------------------------
DROP TABLE IF EXISTS `gpt_conversation_detail`;
CREATE TABLE `gpt_conversation_detail` (
  `id` bigint(20) NOT NULL COMMENT '主键id',
  `conversation_id` varchar(255) DEFAULT NULL COMMENT '会话id',
  `message` text COMMENT '消息内容',
  `type` int(11) DEFAULT NULL COMMENT '类型[1:用户 2:AI]',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id[可以是用户id或者AI角色id]',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_flag` int(11) DEFAULT NULL COMMENT '状态[0:未删除,1:删除]',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='gpt会话详情表';

-- ----------------------------
-- Records of gpt_conversation_detail
-- ----------------------------
BEGIN;
INSERT INTO `gpt_conversation_detail` (`id`, `conversation_id`, `message`, `type`, `user_id`, `create_user`, `create_time`, `update_user`, `update_time`, `delete_flag`) VALUES (1784791281457733634, 'c995c7df-7437-4763-9870-5c832a9e6c53', '你是谁？', 1, 1, NULL, '2024-04-29 11:46:16', NULL, '2024-04-29 11:46:16', 0);
INSERT INTO `gpt_conversation_detail` (`id`, `conversation_id`, `message`, `type`, `user_id`, `create_user`, `create_time`, `update_user`, `update_time`, `delete_flag`) VALUES (1784791281461927937, 'c995c7df-7437-4763-9870-5c832a9e6c53', '\n我是一个人工智能语言模型。我可以帮助您完成多种任务，例如回答问题、提供信息、解释概念等。我的知识和技能来自大量的文本数据和机器学习算法。\n\n我不是一个人，我并没有生物体的身体或情感经历。我的主要目的是帮助和服务于用户，提供高质量的信息和支持。如果您有任何问题或需要帮助，请随时来问我。', 2, 3, NULL, '2024-04-29 11:46:16', NULL, '2024-04-29 11:46:16', 0);
COMMIT;

-- ----------------------------
-- Table structure for social_user
-- ----------------------------
DROP TABLE IF EXISTS `social_user`;
CREATE TABLE `social_user` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `type` int(11) DEFAULT NULL COMMENT '社交平台类型',
  `openid` varchar(255) DEFAULT NULL COMMENT '社交平台openid',
  `token` varchar(255) DEFAULT NULL COMMENT '社交token信息',
  `raw_token_info` varchar(1000) DEFAULT NULL COMMENT '社交原始token信息',
  `username` varchar(255) DEFAULT NULL COMMENT '用户名',
  `nick_name` varchar(255) DEFAULT NULL COMMENT '用户昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '用户头像',
  `raw_user_info` varchar(1000) DEFAULT NULL COMMENT '原始用户信息',
  `code` varchar(255) DEFAULT NULL COMMENT '最后一次认证的code',
  `state` varchar(255) DEFAULT NULL COMMENT '最后一次认证的state',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建用户',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新用户',
  `delete_flag` int(11) DEFAULT '0' COMMENT '是否已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社交用户表';

-- ----------------------------
-- Records of social_user
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for social_user_bind
-- ----------------------------
DROP TABLE IF EXISTS `social_user_bind`;
CREATE TABLE `social_user_bind` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `user_id` bigint(20) DEFAULT NULL COMMENT '系统用户id',
  `user_type` int(11) DEFAULT NULL COMMENT '用户类型',
  `social_user_id` bigint(20) DEFAULT NULL COMMENT '社交账户id',
  `social_type` int(11) DEFAULT NULL COMMENT '社交账户平台',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建用户',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新用户',
  `delete_flag` int(11) DEFAULT '0' COMMENT '是否已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社交用户表关联表';

-- ----------------------------
-- Records of social_user_bind
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_client
-- ----------------------------
DROP TABLE IF EXISTS `sys_client`;
CREATE TABLE `sys_client` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `client_id` varchar(48) NOT NULL COMMENT '客户端id',
  `client_secret` varchar(256) NOT NULL COMMENT '客户端密钥',
  `resource_ids` varchar(256) DEFAULT NULL COMMENT '资源集合',
  `scope` varchar(256) NOT NULL COMMENT '授权范围',
  `authorized_grant_types` varchar(256) NOT NULL COMMENT '授权类型',
  `web_server_redirect_uri` varchar(256) DEFAULT NULL COMMENT '回调地址',
  `authorities` varchar(256) DEFAULT NULL COMMENT '权限',
  `access_token_validity` int(11) NOT NULL COMMENT '令牌过期秒数',
  `refresh_token_validity` int(11) NOT NULL COMMENT '刷新令牌过期秒数',
  `additional_information` varchar(4096) DEFAULT NULL COMMENT '附加说明',
  `autoapprove` varchar(256) DEFAULT NULL COMMENT '自动授权',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `delete_flag` int(11) NOT NULL COMMENT '删除状态[0:未删除,1:删除]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户端表';

-- ----------------------------
-- Records of sys_client
-- ----------------------------
BEGIN;
INSERT INTO `sys_client` (`id`, `client_id`, `client_secret`, `resource_ids`, `scope`, `authorized_grant_types`, `web_server_redirect_uri`, `authorities`, `access_token_validity`, `refresh_token_validity`, `additional_information`, `autoapprove`, `create_user`, `create_time`, `update_user`, `update_time`, `delete_flag`) VALUES (1123598811738675201, 'gpt', '$2a$10$REWTUoZS23tO.oOuRtefWe0JbUt8/sghVsyAEumXUdo0TUQez0Mpq', 'res2', 'all', 'refresh_token,password,authorization_code,captcha,social,client_credentials', 'http://localhost:8888', NULL, 7200, 604800, NULL, NULL, NULL, NULL, NULL, NULL, 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父主键',
  `role_name` varchar(255) DEFAULT NULL COMMENT '角色名',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `role_alias` varchar(255) DEFAULT NULL COMMENT '角色别名',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  `delete_flag` int(11) DEFAULT '0' COMMENT '是否已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `code` varchar(12) DEFAULT NULL COMMENT '用户编号',
  `user_type` varchar(78) DEFAULT NULL COMMENT '用户平台',
  `account` varchar(45) DEFAULT NULL COMMENT '账号',
  `password` varchar(74) DEFAULT NULL COMMENT '密码',
  `name` varchar(20) DEFAULT NULL COMMENT '昵称',
  `real_name` varchar(10) DEFAULT NULL COMMENT '真名',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像',
  `email` varchar(45) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(45) DEFAULT NULL COMMENT '手机',
  `birthday` datetime DEFAULT NULL COMMENT '生日',
  `sex` int(11) DEFAULT NULL COMMENT '性别',
  `role_id` varchar(1000) DEFAULT NULL COMMENT '角色id',
  `dept_id` varchar(1000) DEFAULT NULL COMMENT '部门id',
  `post_id` varchar(1000) DEFAULT NULL COMMENT '岗位id',
  `tenant_id` int(11) DEFAULT NULL COMMENT '租户id',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `delete_flag` int(11) DEFAULT '0' COMMENT '状态[0:未删除,1:删除]',
  `status` int(11) DEFAULT '0' COMMENT '状态:[0:未激活 1:激活 2:禁用]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` (`id`, `code`, `user_type`, `account`, `password`, `name`, `real_name`, `avatar`, `email`, `phone`, `birthday`, `sex`, `role_id`, `dept_id`, `post_id`, `tenant_id`, `create_user`, `create_time`, `update_user`, `update_time`, `delete_flag`, `status`) VALUES (1, '', '[1,2]', 'admin', '$2a$10$CpDYKGj8tUA49fdOo69V..PwKJV4VbAt6MzQ/T8ro5N4nGINIJuGC', '骑着蜗牛去旅行', NULL, 'https://gitee.com/assets/no_portrait.png', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, 1);
INSERT INTO `sys_user` (`id`, `code`, `user_type`, `account`, `password`, `name`, `real_name`, `avatar`, `email`, `phone`, `birthday`, `sex`, `role_id`, `dept_id`, `post_id`, `tenant_id`, `create_user`, `create_time`, `update_user`, `update_time`, `delete_flag`, `status`) VALUES (2, '', '[1,2]', 'admin1', '$2a$10$CpDYKGj8tUA49fdOo69V..PwKJV4VbAt6MzQ/T8ro5N4nGINIJuGC', '测试', NULL, 'https://gitee.com/assets/no_portrait.png', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, 1);
COMMIT;

-- ----------------------------
-- Table structure for user_gpt
-- ----------------------------
DROP TABLE IF EXISTS `user_gpt`;
CREATE TABLE `user_gpt` (
  `id` bigint(20) NOT NULL COMMENT '主键id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id(-1为系统默认配置，非用户)',
  `regular_credit_limit` int(11) DEFAULT NULL COMMENT '普通额度',
  `subscription_credit_limit` int(11) DEFAULT NULL COMMENT '订阅额度',
  `open_token` text COMMENT '订阅token',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_flag` int(11) DEFAULT '0' COMMENT '状态[0:未删除,1:删除]',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户gpt管理表';

-- ----------------------------
-- Records of user_gpt
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
