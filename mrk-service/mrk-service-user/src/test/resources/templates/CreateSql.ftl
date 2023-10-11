-- ----------------------------
-- Table structure for ${create.tableName}
-- ----------------------------
DROP TABLE IF EXISTS `${create.tableName}`;
CREATE TABLE `${create.tableName}`  (
`id` bigint(20) NOT NULL COMMENT '主键',
`tenant_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '000000' COMMENT '租户ID',
`code` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户编号',
`user_type` int(11) NULL DEFAULT NULL COMMENT '用户平台',
`account` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
`password` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
`name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
`real_name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '真名',
`avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
`email` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
`phone` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机',
`birthday` datetime NULL DEFAULT NULL COMMENT '生日',
`sex` int(11) NULL DEFAULT NULL COMMENT '性别',
`role_id` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色id',
`dept_id` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门id',
`post_id` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '岗位id',
`create_user` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
`create_dept` bigint(20) NULL DEFAULT NULL COMMENT '创建部门',
`create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
`update_user` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
`update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
`status` int(11) NULL DEFAULT NULL COMMENT '状态',
`is_deleted` int(11) NULL DEFAULT 0 COMMENT '是否已删除',
PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;