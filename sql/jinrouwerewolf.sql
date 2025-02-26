/*
 Navicat Premium Data Transfer

 Source Server         : fys04
 Source Server Type    : MySQL
 Source Server Version : 80100
 Source Host           : localhost:3306
 Source Schema         : jinrouwerewolf

 Target Server Type    : MySQL
 Target Server Version : 80100
 File Encoding         : 65001

 Date: 18/02/2025 07:35:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for anonymous
-- ----------------------------
DROP TABLE IF EXISTS `anonymous`;
CREATE TABLE `anonymous`  (
  `anonymous_name_id` int NOT NULL AUTO_INCREMENT COMMENT '匿名房间表id',
  `anony_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '匿名角色名',
  `theme` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '匿名房间主题',
  `icon_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '房间头像url',
  PRIMARY KEY (`anonymous_name_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of anonymous
-- ----------------------------

-- ----------------------------
-- Table structure for game_settings
-- ----------------------------
DROP TABLE IF EXISTS `game_settings`;
CREATE TABLE `game_settings`  (
  `game_setting_id` int NOT NULL AUTO_INCREMENT COMMENT '游戏设定表id',
  `is_first_victims` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否有首日牺牲者安全性',
  `is_hope_mode` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否是希望职业制',
  `day_duration` int NULL DEFAULT NULL COMMENT '白天长度',
  `night_duration` int NULL DEFAULT NULL COMMENT '夜晚长度',
  `vote_duration` int NULL DEFAULT NULL COMMENT '投票时间长度',
  `morning_duration` int NULL DEFAULT NULL COMMENT '犹豫\\黎明时间长度',
  `n_second_rule` int NULL DEFAULT NULL COMMENT '到达白天后的禁言时间长度',
  `hunter_continuous_guarding` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '猎人是否允许连续护卫',
  `identity_list` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '设定的职业列表',
  `gm_mode` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否gm制',
  PRIMARY KEY (`game_setting_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of game_settings
-- ----------------------------

-- ----------------------------
-- Table structure for lobby_message
-- ----------------------------
DROP TABLE IF EXISTS `lobby_message`;
CREATE TABLE `lobby_message`  (
  `lobby_message_id` int NOT NULL AUTO_INCREMENT COMMENT '大厅公告表',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '正文内容',
  `publish_user_id` int NULL DEFAULT NULL COMMENT '发布者的用户id',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  PRIMARY KEY (`lobby_message_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of lobby_message
-- ----------------------------
INSERT INTO `lobby_message` VALUES (1, '第一条', 10, '2025-02-04 01:32:07');
INSERT INTO `lobby_message` VALUES (2, '第二条', 12, '2025-02-28 01:32:24');

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `message_id` int NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `room_id` int NULL DEFAULT NULL COMMENT '消息所在的房间ID',
  `user_id` int NULL DEFAULT NULL COMMENT '消息发出者ID',
  `player_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发言者名字',
  `message_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '消息种类\r\n1-GM、\r\n2-DAY_TALK、\r\n3-WOLF_TALK、\r\n4-KYOUYUU_TALK、\r\n5-KITSUNE_TALK、\r\n6-WATCH_TALK、\r\n7-SELF_TALK',
  `message_index` bigint NULL DEFAULT NULL COMMENT '发言索引',
  `message_gm_to_player_id` int NULL DEFAULT NULL COMMENT 'GM对话玩家编号（playerId',
  `message_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '消息发布时间',
  `message_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '消息正文',
  `font_color` varchar(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文字颜色',
  `font_size` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文字大小',
  `day_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '第几天和白天或夜晚',
  `reply_to` int NULL DEFAULT NULL COMMENT '回复索引',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `fk_message_room_id`(`room_id`) USING BTREE,
  INDEX `fk_message_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8989 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message
-- ----------------------------

-- ----------------------------
-- Table structure for player
-- ----------------------------
DROP TABLE IF EXISTS `player`;
CREATE TABLE `player`  (
  `player_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '用户id',
  `room_player_id` int NULL DEFAULT NULL COMMENT '房间内的几号玩家',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名字',
  `icon_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '匿名头像url',
  `room_id` int NULL DEFAULT NULL COMMENT '房间id',
  `identity` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '职业表中的职业ID',
  `is_alive` int NULL DEFAULT NULL COMMENT '是否生存',
  `is_ready` int NULL DEFAULT NULL COMMENT '是否准备',
  `result` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '胜负',
  PRIMARY KEY (`player_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `fk_identity_id`(`identity`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 937 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of player
-- ----------------------------

-- ----------------------------
-- Table structure for room
-- ----------------------------
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room`  (
  `room_id` int NOT NULL AUTO_INCREMENT COMMENT '房间ID',
  `room_state` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '房间状态\r\n1-ACTIVE、\r\n2-OLD、\r\n3-PLAYING、\r\n4-ENDED、\r\n5-DISCARDED',
  `room_creator_id` int NULL DEFAULT NULL COMMENT '房主ID',
  `winner` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '胜利者',
  `game_setting_id` int NULL DEFAULT NULL COMMENT '游戏设置表id',
  `room_create_time` datetime NULL DEFAULT NULL COMMENT '房间创建时间',
  `room_end_time` datetime NULL DEFAULT NULL COMMENT '房间结束时间（游戏结束或废村',
  `room_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '房间标题',
  `room_description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '房间描述',
  `is_anonymous` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '匿名\r\n1-NO\r\n2-WEAK\r\n3-STRONG',
  `is_locked` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否密码\r\n1-FALSE\r\n2-TRUE',
  `room_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '有密码的情况下的房间密码',
  `village_rule` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '村规',
  PRIMARY KEY (`room_id`) USING BTREE,
  INDEX `fk_room_creater_id_user_id`(`room_creator_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of room
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '账号',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
  `user_state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户状态',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone_number` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电话号码',
  `icon_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像url',
  `open_history` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否公开战绩',
  `bio` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '简介',
  `user_create_time` datetime NULL DEFAULT NULL COMMENT '用户创建时间',
  `user_last_online_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户最后上线时间',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
