/*
 Navicat Premium Data Transfer

 Source Server         : MYSQL
 Source Server Type    : MySQL
 Source Server Version : 80015
 Source Host           : localhost:3306
 Source Schema         : apollo

 Target Server Type    : MySQL
 Target Server Version : 80015
 File Encoding         : 65001

 Date: 18/05/2019 16:10:23
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_node
-- ----------------------------
DROP TABLE IF EXISTS `t_node`;
CREATE TABLE `t_node`  (
  `nodeid` bigint(36) NOT NULL,
  `pnodeid` bigint(36) NULL DEFAULT NULL,
  `weights` int(6) NULL DEFAULT NULL COMMENT '权重',
  `nodename` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '节点名称',
  `nodetype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '节点类型',
  `descripte` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '节点描述',
  `isdelete` tinyint(2) NULL DEFAULT NULL COMMENT '0 删除',
  `pxh` int(6) NULL DEFAULT NULL COMMENT '排序号',
  `nodeicon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '节点图标',
  `nodeurl` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '节点地址',
  `gmt_create` bigint(20) NOT NULL COMMENT '数据创建时间戳',
  `gmt_remove` tinyint(1) NOT NULL COMMENT '数据删除标识',
  `gmt_modified` bigint(20) NOT NULL COMMENT '数据修改时间戳',
  `gmt_version` int(11) NOT NULL COMMENT '数据版本号',
  PRIMARY KEY (`nodeid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_node
-- ----------------------------
INSERT INTO `t_node` VALUES (0, 0, 0, '主页', 'homepage', '主页', 1, 0, 'fa fa-home', '/portal', 20181112144600, 0, 20181112144600, 1);
INSERT INTO `t_node` VALUES (20181112144600, 0, 1, '用户管理', 'user_manage', '管理用户的资料、好友、社团等信息', 1, 1, 'fa fa-users', NULL, 20181112144600, 0, 20181112144600, 1);
INSERT INTO `t_node` VALUES (20181112144601, 20181112144600, 2, '个人资料', 'personal_info', '个人信息', 1, 1, 'fa fa-vcard-o', '/profile', 20181112144600, 0, 20181112144600, 1);
INSERT INTO `t_node` VALUES (20181112144602, 20181112144600, 3, '关注列表', 'friends_list', '关注列表', 1, 2, 'fa fa-heartbeat', '', 20181112144600, 0, 20181112144600, 1);
INSERT INTO `t_node` VALUES (20181112144604, 0, 5, '博客管理', 'blog_manage', '管理博客', 1, 2, 'fa fa-wrench', NULL, 20181112144600, 0, 20181112144600, 1);
INSERT INTO `t_node` VALUES (20181112144605, 20181112144609, 6, '角色节点管理', 'role_manage', '管理角色节点', 1, 1, 'fa fa-user-secret', '/roleManage', 20181112144600, 0, 20181112144600, 1);
INSERT INTO `t_node` VALUES (20181112144606, 20181112144609, 7, '用户节点管理', 'node_manage', '管理用户节点', 1, 2, 'fa fa-sitemap', '/nodeManage', 20181112144600, 0, 20181112144600, 1);
INSERT INTO `t_node` VALUES (20181112144607, 20181112144604, 8, '公告', 'announcement_manage', '管理全站公告', 1, 1, 'fa fa-commenting-o', NULL, 20181112144600, 0, 20181112144600, 1);
INSERT INTO `t_node` VALUES (20181112144608, 0, 9, '信箱', 'mailbox', '信箱', 1, 3, 'fa fa-envelope', '', 20181112144608, 0, 20181112144608, 1);
INSERT INTO `t_node` VALUES (20181112144609, 0, 10, '系统管理', 'setting_manage', '系统管理', 1, 4, 'fa fa-cogs', NULL, 20181216144608, 0, 20181216144608, 1);
INSERT INTO `t_node` VALUES (20181112144610, 20181112144609, 11, '节点设置', 'node_setting', '设置系统节点', 1, 4, 'fa fa-code-fork', '/nodeLinkManage', 20181216144608, 0, 20181216144608, 1);
INSERT INTO `t_node` VALUES (20181112144611, 20181112144609, 12, '用户角色管理', 'user_role', '管理用户角色', 1, 3, 'fa fa-user-times', NULL, 20181222144608, 0, 20181222144608, 1);
INSERT INTO `t_node` VALUES (20181112144612, 20181112144604, 13, '博客编辑', 'blog_edit', '博客编辑', 1, 2, 'fa fa-edit', '/simple', 20181222144608, 0, 20181222144608, 1);
INSERT INTO `t_node` VALUES (1082460179111829505, 0, 15, '工具箱', 'toolbox', '工具', 1, 6, 'fa fa-briefcase', '', 20190108101312, 0, 20190108101312, 1);
INSERT INTO `t_node` VALUES (1082462702270898177, 1082460179111829505, 16, '图片管理', 'webpage_screenshot', '图片管理', 1, 0, 'fa fa-picture-o', '', 20190108102303, 0, 20190108102303, 1);
INSERT INTO `t_node` VALUES (1082588103626608641, 1082460179111829505, 17, '机器人', 'robot', '机器人', 1, 1, 'fa fa-github-alt', '', 20190108184132, 0, 20190108184132, 1);
INSERT INTO `t_node` VALUES (1084018300875587585, 20181112144609, 18, '服务管理', 'sys_server', '服务管理', 1, 5, 'fa fa-tasks', '/schedulejob', 20190112172437, 0, 20190112172437, 1);
INSERT INTO `t_node` VALUES (1113055845380988929, 1082460179111829505, 19, 'BILIBILI', 'bilibili', '哔哩哔哩 (゜-゜)つロ 干杯~-bilibili', 1, 2, 'fa fa-simplybuilt', '/bilibili', 20190402202927, 0, 20190402202927, 1);

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order`  (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `goods_id` bigint(20) NOT NULL COMMENT '资料id',
  `order_status` int(11) NOT NULL COMMENT '订单状态',
  `pay_mode` int(11) NOT NULL COMMENT '支付方式',
  `currency` int(11) NOT NULL COMMENT '下载币',
  `price` int(11) NOT NULL COMMENT '现金',
  `pay_currency` int(11) NOT NULL COMMENT '支付下载币',
  `pay_price` int(11) NOT NULL COMMENT '支付现金',
  `gmt_create` bigint(20) NOT NULL COMMENT '数据创建时间戳',
  `gmt_remove` tinyint(1) NOT NULL COMMENT '数据删除标识',
  `gmt_modified` bigint(20) NOT NULL COMMENT '数据修改时间戳',
  `gmt_version` int(11) NOT NULL COMMENT '数据版本号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role`  (
  `id` bigint(32) NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `weights` bigint(36) NOT NULL DEFAULT 0 COMMENT '节点权重',
  `edit` tinyint(2) NOT NULL DEFAULT 0 COMMENT '允许编辑 0 是 1 不是',
  `gmt_create` bigint(20) NOT NULL COMMENT '数据创建时间戳',
  `gmt_remove` tinyint(1) NOT NULL COMMENT '数据删除标识',
  `gmt_modified` bigint(20) NOT NULL COMMENT '数据修改时间戳',
  `gmt_version` int(11) NOT NULL COMMENT '数据版本号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_role
-- ----------------------------
INSERT INTO `t_role` VALUES (1, '超级管理员', '超级管理员', 1032175, 1, 20181112144600, 0, 1555142209499, 3);
INSERT INTO `t_role` VALUES (2, '团队管理员', '管理团队人员', 767999, 0, 20181112144600, 0, 20181112144600, 1);
INSERT INTO `t_role` VALUES (3, '创建者', '编辑文档和上传文件等操作', 762687, 1, 20181112144600, 0, 20181112144600, 1);
INSERT INTO `t_role` VALUES (1076389914520801281, '普通用户', '普通用户', 754495, 0, 20181112144600, 0, 20181112144600, 1);

-- ----------------------------
-- Table structure for t_rz
-- ----------------------------
DROP TABLE IF EXISTS `t_rz`;
CREATE TABLE `t_rz`  (
  `zrid` bigint(36) NOT NULL,
  `userid` bigint(36) NULL DEFAULT NULL,
  `login_time` bigint(20) NULL DEFAULT NULL,
  `action` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `target_type` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '目标类型',
  `remark` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标记',
  `ip` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'IP',
  `login_type` tinyint(2) NULL DEFAULT NULL COMMENT '登陆类型 1 pc登陆 2 移动端登陆',
  PRIMARY KEY (`zrid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_rz
-- ----------------------------
INSERT INTO `t_rz` VALUES (1118038319205199874, 1, 1555396082013, '/api/login', 'POST', '登录', '127.0.0.1', 1);
INSERT INTO `t_rz` VALUES (1118046570449776642, 1, 1555398049260, '/api/login', 'POST', '登录', '127.0.0.1', 1);
INSERT INTO `t_rz` VALUES (1118048686539067394, 1, 1555398553777, '/api/login', 'POST', '登录', '127.0.0.1', 1);
INSERT INTO `t_rz` VALUES (1118135647616057346, 1, 1555419286814, '/api/login', 'POST', '登录', '127.0.0.1', 1);

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` bigint(36) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `salt` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地址',
  `is_disabled` tinyint(1) NOT NULL COMMENT '禁止登录',
  `gmt_create` bigint(20) NOT NULL COMMENT '数据创建时间戳',
  `gmt_remove` tinyint(1) NOT NULL COMMENT '数据删除标识',
  `gmt_modified` bigint(20) NOT NULL COMMENT '数据修改时间戳',
  `gmt_version` int(11) NOT NULL COMMENT '数据版本号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1062534034624811011 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '个人资料' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (1, 'admin', 'cb3b9e00f09862cfac05dfa6f8182ca3', '21232f297a5735a783894a0e4a801fc3', '《关于我转生变成史莱姆这档事 》 [1]  是伏瀬著作、みっつばー插画，GC NOVELS所属的轻小说。亦有同名改编漫画。\r\n单行本日文版由マイクロマガジン社出版发行，繁体中文版由台湾角川代理发行。\r\n至日文版单行本第7卷发售时，累计销量突破40万部。 [2] ', '提督', '上海市闵行区绿地科技岛广场A座2606室', 0, 1553161356557, 0, 1553243009833, 1);
INSERT INTO `t_user` VALUES (2, 'hx', '1f647a73cca520de6dc8cda7604fa9ec', '302b1c28b9ce3f53829fc094a68c02ca', NULL, '神主', NULL, 0, 1553243026311, 0, 1553243026311, 1);
INSERT INTO `t_user` VALUES (3, 'green025275100101@outlook.com', '3fb5bc6b3191c3eabc6b2c522842b679', 'f3d50b06bbf13221bf16631d17d1a422', NULL, '森夏', NULL, 0, 1553243122898, 0, 1553243122898, 1);
INSERT INTO `t_user` VALUES (4, 'app025275@outlook.com', '2e0d2ca3eaab116c963744feaa9ea9b4', '5c3113fc0ed43b57973d1b4ec29efbc6', NULL, '花泽香菜', NULL, 0, 1553567014660, 0, 1553567101359, 1);
INSERT INTO `t_user` VALUES (1062534034624811010, '1714167268@qq.com', 'be14c0ef1303207fc32e577cd993b578', '05e60006b490309fa60f49fb72b9ac1c', NULL, '泽野弘之', NULL, 0, 1553740130128, 0, 1553840873619, 1);

-- ----------------------------
-- Table structure for t_user_weights
-- ----------------------------
DROP TABLE IF EXISTS `t_user_weights`;
CREATE TABLE `t_user_weights`  (
  `user_id` bigint(36) NOT NULL,
  `node_weights` bigint(36) NOT NULL DEFAULT 0 COMMENT '节点权重',
  `role_id` bigint(36) NOT NULL DEFAULT 0 COMMENT '角色id',
  `gmt_create` bigint(20) NOT NULL COMMENT '数据创建时间戳',
  `gmt_remove` tinyint(1) NOT NULL COMMENT '数据删除标识',
  `gmt_modified` bigint(20) NOT NULL COMMENT '数据修改时间戳',
  `gmt_version` int(11) NOT NULL COMMENT '数据版本号',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '人员节点表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user_weights
-- ----------------------------
INSERT INTO `t_user_weights` VALUES (1, 1032175, 1, 0, 0, 0, 0);
INSERT INTO `t_user_weights` VALUES (2, 1032175, 2, 0, 0, 1555145871891, 2);
INSERT INTO `t_user_weights` VALUES (3, 754495, 3, 0, 0, 0, 0);
INSERT INTO `t_user_weights` VALUES (4, 754687, 3, 0, 0, 0, 0);
INSERT INTO `t_user_weights` VALUES (1062534034624811010, 754687, 3, 0, 0, 0, 0);

SET FOREIGN_KEY_CHECKS = 1;
