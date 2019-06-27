/*
 Navicat Premium Data Transfer

 Source Server         : wenli
 Source Server Type    : MySQL
 Source Server Version : 50726
 Source Host           : localhost:3306
 Source Schema         : spoc

 Target Server Type    : MySQL
 Target Server Version : 50726
 File Encoding         : 65001

 Date: 19/06/2019 15:33:14
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for achievement
-- ----------------------------
DROP TABLE IF EXISTS `achievement`;
CREATE TABLE `achievement`  (
  `achievement_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `achievement_category` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '拓展任务，开放实验，毕业设计，大创，创新创业，竞赛',
  `achievement_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `achievement_photo` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `achievement_detail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `guidance_teacher` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `member` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `compere` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `team_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `level` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '校级一般，重点，省级一般，重点',
  `approval_time` datetime(0) NULL DEFAULT NULL COMMENT '批准时间',
  `finish_time` datetime(0) NULL DEFAULT NULL,
  `achievement_accessory` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `fund` int(11) NULL DEFAULT NULL,
  `feature` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '特色',
  `shared_value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '共享价值',
  `is_share` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否共享',
  `turnover` int(11) NULL DEFAULT NULL COMMENT '成交量',
  PRIMARY KEY (`achievement_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for achievement_accessory
-- ----------------------------
DROP TABLE IF EXISTS `achievement_accessory`;
CREATE TABLE `achievement_accessory`  (
  `accessory_id` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT '附件id',
  `author_id` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '作者id',
  `accessory_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '附件名',
  `accessory_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '附件保存路径',
  `accessory_time` date NULL DEFAULT NULL COMMENT '附件提交时间',
  `achievement_id` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `delete_flag` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`accessory_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for achievement_course_expand
-- ----------------------------
DROP TABLE IF EXISTS `achievement_course_expand`;
CREATE TABLE `achievement_course_expand`  (
  `achievement_id` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `course_id` varchar(35) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `achievement_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `achievement_category` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `introduction` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `achievement_detail` longtext CHARACTER SET utf8 COLLATE utf8_bin NULL,
  `guidance_teacher` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `member` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `compere` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `team_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `finish_time` date NULL DEFAULT NULL,
  `upload_time` date NULL DEFAULT NULL,
  `first_picture` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '第一张图片',
  `browse_volume` int(11) NULL DEFAULT NULL,
  `member_num` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '学生学号',
  `upload_author` varchar(45) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '上传者ID',
  `delete_flag` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`achievement_id`) USING BTREE,
  INDEX `fk_id_idx`(`course_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for achievement_graduation_design_for_college_students
-- ----------------------------
DROP TABLE IF EXISTS `achievement_graduation_design_for_college_students`;
CREATE TABLE `achievement_graduation_design_for_college_students`  (
  `achievement_id` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `achievement_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `achievement_category` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `introduction` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `achievement_detail` longtext CHARACTER SET utf8 COLLATE utf8_bin NULL,
  `guidance_teacher` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `compere` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '项目负责人',
  `finish_time` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `upload_time` date NULL DEFAULT NULL,
  `first_picture` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `browse_volume` int(11) NULL DEFAULT NULL,
  `member_num` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '学生学号',
  `upload_author` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `delete_flag` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`achievement_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for achievement_industry_university_research_project
-- ----------------------------
DROP TABLE IF EXISTS `achievement_industry_university_research_project`;
CREATE TABLE `achievement_industry_university_research_project`  (
  `project_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `project_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `project_detail` longtext CHARACTER SET utf8 COLLATE utf8_bin NULL,
  `compere` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '主持人',
  `member` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `collect_the_amount` int(11) NULL DEFAULT NULL COMMENT '进账金额',
  `cooperator` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '合作单位',
  `start_time` datetime(0) NULL DEFAULT NULL,
  `end_time` datetime(0) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `first_picture` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `is_share` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `project_category` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '项目类型',
  `introduction` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `browse_volume` int(11) NULL DEFAULT NULL,
  `price` float NULL DEFAULT NULL,
  `member_num` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `upload_author` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `delete_flag` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`project_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for achievement_picture
-- ----------------------------
DROP TABLE IF EXISTS `achievement_picture`;
CREATE TABLE `achievement_picture`  (
  `accessory_id` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `author_id` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `accessory_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `accessory_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `accessory_time` date NULL DEFAULT NULL,
  `achievement_id` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `delete_flag` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`accessory_id`) USING BTREE,
  INDEX `fk_achievement_picture_1_idx`(`achievement_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for achievement_score
-- ----------------------------
DROP TABLE IF EXISTS `achievement_score`;
CREATE TABLE `achievement_score`  (
  `id` varchar(45) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `user_id` varchar(45) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `score` double NULL DEFAULT NULL,
  `achievement_id` varchar(45) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `time` date NULL DEFAULT NULL,
  `category` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for achievement_student_innovation_and_entrepreneurship
-- ----------------------------
DROP TABLE IF EXISTS `achievement_student_innovation_and_entrepreneurship`;
CREATE TABLE `achievement_student_innovation_and_entrepreneurship`  (
  `achievement_id` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `achievement_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `achievement_category` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `introduction` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `achievement_detail` longtext CHARACTER SET utf8 COLLATE utf8_bin NULL,
  `guidance_teacher` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `member` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `compere` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `team_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `level` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `feature` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `finish_time` date NULL DEFAULT NULL,
  `plan` longtext CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT '计划',
  `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '状态',
  `declaration` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '申报书，申报书一份，只保存路径',
  `midreply` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '中期答辩',
  `concluding_report` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '结题报告',
  `upload_time` date NULL DEFAULT NULL COMMENT '成果上传时间',
  `first_picture` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `browse_volume` int(11) NULL DEFAULT NULL,
  `member_num` longtext CHARACTER SET utf8 COLLATE utf8_bin NULL,
  `upload_author` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `delete_flag` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`achievement_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for achievements_of_college_students_competition
-- ----------------------------
DROP TABLE IF EXISTS `achievements_of_college_students_competition`;
CREATE TABLE `achievements_of_college_students_competition`  (
  `achievement_id` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `achievement_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `achievement_category` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `achievement_introduction` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `ahievement_detail` longtext CHARACTER SET utf8 COLLATE utf8_bin NULL,
  `guidance_teacher` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `member` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '团队成员',
  `compere` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '主持人（项目负责人）',
  `team_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `level` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '等级：校级、省级、国急',
  `feature` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '特点',
  `finish_time` date NULL DEFAULT NULL COMMENT '作品完成时间',
  `upload_time` date NULL DEFAULT NULL COMMENT '上传成果时间',
  `first_picture` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `browse_volume` int(11) NULL DEFAULT NULL,
  `member_num` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `upload_author` varchar(45) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `delete_flag` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`achievement_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for achievment_comment
-- ----------------------------
DROP TABLE IF EXISTS `achievment_comment`;
CREATE TABLE `achievment_comment`  (
  `comment_id` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `achievement_id` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `author_id` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `comment_content` longtext CHARACTER SET utf8 COLLATE utf8_bin NULL,
  `uploadTime` date NULL DEFAULT NULL,
  `achievement_category` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `author_picture` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `author_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  PRIMARY KEY (`comment_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `adminId` int(11) NOT NULL AUTO_INCREMENT,
  `adminPassword` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `adminUsername` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`adminId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for att_leave
-- ----------------------------
DROP TABLE IF EXISTS `att_leave`;
CREATE TABLE `att_leave`  (
  `attendance_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `student_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '请假人id',
  PRIMARY KEY (`attendance_id`, `student_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for attence
-- ----------------------------
DROP TABLE IF EXISTS `attence`;
CREATE TABLE `attence`  (
  `attendance_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `student_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`attendance_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for attendance
-- ----------------------------
DROP TABLE IF EXISTS `attendance`;
CREATE TABLE `attendance`  (
  `attendance_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `virtual_class_num` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `index` int(11) NULL DEFAULT NULL COMMENT '考勤序号',
  `attendance_time` datetime(0) NULL DEFAULT NULL COMMENT '考勤时间',
  `attendance_num` int(11) NULL DEFAULT NULL COMMENT '签到人数',
  `total_num` int(11) NULL DEFAULT NULL COMMENT '总人数',
  `leave_num` int(11) NULL DEFAULT NULL COMMENT '请假人数',
  `truancy_num` int(11) NULL DEFAULT NULL COMMENT '旷课人数',
  PRIMARY KEY (`attendance_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `category_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `category_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `category_detail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `category_num` int(11) NULL DEFAULT NULL COMMENT '系部编号（5系）',
  PRIMARY KEY (`category_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for class_task
-- ----------------------------
DROP TABLE IF EXISTS `class_task`;
CREATE TABLE `class_task`  (
  `virtual_class_num` varchar(35) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `task_id` varchar(35) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `up_student_num` int(11) NULL DEFAULT NULL,
  `task_end_time` datetime(0) NOT NULL,
  `pre_task_end_time` datetime(0) NULL DEFAULT NULL COMMENT '课前任务截至时间',
  PRIMARY KEY (`virtual_class_num`, `task_id`) USING BTREE,
  INDEX `task_id1`(`task_id`) USING BTREE,
  CONSTRAINT `task_id` FOREIGN KEY (`task_id`) REFERENCES `task` (`task_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `virtual_class_num2` FOREIGN KEY (`virtual_class_num`) REFERENCES `virtual_class` (`virtual_class_num`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
  `course_id` varchar(35) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `course_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `course_detail` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `course_category` int(11) NOT NULL,
  `publish_time` datetime(0) NULL DEFAULT NULL,
  `publisher_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `face_img` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `fine` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '精品课程',
  `course_student_num` int(11) NULL DEFAULT NULL COMMENT '每门课的总人数',
  PRIMARY KEY (`course_id`) USING BTREE,
  INDEX `course_category`(`course_category`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for industry_university_research_project
-- ----------------------------
DROP TABLE IF EXISTS `industry_university_research_project`;
CREATE TABLE `industry_university_research_project`  (
  `project_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `project_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `project_detail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `compere` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主持人',
  `member` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `collect_the_amount` int(11) NULL DEFAULT NULL COMMENT '进账金额',
  `cooperator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '合作单位',
  `start_time` datetime(0) NULL DEFAULT NULL,
  `end_time` datetime(0) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `project_photo` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `is_share` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `project_category` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '项目类型',
  PRIMARY KEY (`project_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for paper
-- ----------------------------
DROP TABLE IF EXISTS `paper`;
CREATE TABLE `paper`  (
  `paper_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `paper_title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `summary` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `published_journals` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发表期刊',
  `journals_photo` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '期刊首页，目录页，论文首页图片，逗号分隔',
  `publishing_house` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '出版社',
  `publish_time` datetime(0) NULL DEFAULT NULL,
  `author` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `organization` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '单位',
  `relevant_project` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '相关项目',
  `is_share` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否公开',
  `is_core` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否为核心',
  PRIMARY KEY (`paper_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for point
-- ----------------------------
DROP TABLE IF EXISTS `point`;
CREATE TABLE `point`  (
  `point_id` int(11) NOT NULL AUTO_INCREMENT,
  `task_category` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `point_value` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`point_id`) USING BTREE,
  INDEX `task_category`(`task_category`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for prize
-- ----------------------------
DROP TABLE IF EXISTS `prize`;
CREATE TABLE `prize`  (
  `prize_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '获奖id',
  `prize_project_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '获奖项目名称',
  `prize_level` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '获奖级别',
  `that_organization` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '认定单位',
  `compère` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主持人',
  `member` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `prize_time` datetime(0) NULL DEFAULT NULL COMMENT '授奖时间',
  `project_time` datetime(0) NULL DEFAULT NULL,
  `is_share` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否公开',
  `certificate_photo` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '证书图片',
  PRIMARY KEY (`prize_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pub_accessory
-- ----------------------------
DROP TABLE IF EXISTS `pub_accessory`;
CREATE TABLE `pub_accessory`  (
  `pub_accessory_id` int(11) NOT NULL AUTO_INCREMENT,
  `pub_accessory_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pub_accessory_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pub_accessory_time` datetime(0) NULL DEFAULT NULL,
  `task_id` varchar(35) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `delete_flag` int(1) NULL DEFAULT NULL,
  PRIMARY KEY (`pub_accessory_id`) USING BTREE,
  INDEX `task_id1`(`task_id`) USING BTREE,
  CONSTRAINT `task_id1` FOREIGN KEY (`task_id`) REFERENCES `task` (`task_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for real_class
-- ----------------------------
DROP TABLE IF EXISTS `real_class`;
CREATE TABLE `real_class`  (
  `real_class_num` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `real_class_category` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `real_person_num` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`real_class_num`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for real_virtual_class
-- ----------------------------
DROP TABLE IF EXISTS `real_virtual_class`;
CREATE TABLE `real_virtual_class`  (
  `real_class_num` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `virtual_class_num` varchar(35) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`virtual_class_num`, `real_class_num`) USING BTREE,
  INDEX `real_class_num1`(`real_class_num`) USING BTREE,
  CONSTRAINT `real_class_num1` FOREIGN KEY (`real_class_num`) REFERENCES `real_class` (`real_class_num`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `virtual_class_num1` FOREIGN KEY (`virtual_class_num`) REFERENCES `virtual_class` (`virtual_class_num`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for resource
-- ----------------------------
DROP TABLE IF EXISTS `resource`;
CREATE TABLE `resource`  (
  `resource_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `resource_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `resource_detail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `publish_time` datetime(0) NULL DEFAULT NULL,
  `publisher_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `course_id` varchar(35) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `img_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `resource_type_id` int(11) NOT NULL,
  `size` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `time_size` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `watch_num` int(11) NULL DEFAULT NULL COMMENT '查看次数，(发布资源的班级人数，不在籍人员点一次加一次)',
  `use_num` int(11) NULL DEFAULT NULL COMMENT '教师使用的次数',
  PRIMARY KEY (`resource_id`) USING BTREE,
  INDEX `category_id1`(`course_id`) USING BTREE,
  INDEX `reource_type_id`(`resource_type_id`) USING BTREE,
  CONSTRAINT `course_id2` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `reource_type_id` FOREIGN KEY (`resource_type_id`) REFERENCES `resource_type` (`resource_type_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for resource_type
-- ----------------------------
DROP TABLE IF EXISTS `resource_type`;
CREATE TABLE `resource_type`  (
  `resource_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `resource_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`resource_type_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sign_in
-- ----------------------------
DROP TABLE IF EXISTS `sign_in`;
CREATE TABLE `sign_in`  (
  `sign_id` varchar(35) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `cource_id` varchar(35) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `sign_point` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`sign_id`) USING BTREE,
  INDEX `course_id`(`cource_id`) USING BTREE,
  CONSTRAINT `course_id` FOREIGN KEY (`cource_id`) REFERENCES `course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student`  (
  `student_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `student_name` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `student_gender` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `student_password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `real_class_num` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `professional` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `telephone` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `face_img` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `student_nickname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `student_note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注，存放的是学生留级后，之前的学号',
  `student_category` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '学生所属系部',
  PRIMARY KEY (`student_id`) USING BTREE,
  INDEX `student_category`(`student_category`) USING BTREE,
  CONSTRAINT `student_category` FOREIGN KEY (`student_category`) REFERENCES `category` (`category_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for student_cource_point
-- ----------------------------
DROP TABLE IF EXISTS `student_cource_point`;
CREATE TABLE `student_cource_point`  (
  `student_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `cource_id` int(11) NOT NULL,
  `cource_point` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`student_id`, `cource_id`) USING BTREE,
  INDEX `cource_id2`(`cource_id`) USING BTREE,
  CONSTRAINT `student_id` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for student_course
-- ----------------------------
DROP TABLE IF EXISTS `student_course`;
CREATE TABLE `student_course`  (
  `student_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `course_id` varchar(35) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `class_grade` int(11) NULL DEFAULT NULL,
  `manager` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '关注为2，不关注为0',
  PRIMARY KEY (`student_id`, `course_id`, `manager`) USING BTREE,
  INDEX `course_id4`(`course_id`) USING BTREE,
  CONSTRAINT `course_id4` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `student_id2` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for student_sign_point
-- ----------------------------
DROP TABLE IF EXISTS `student_sign_point`;
CREATE TABLE `student_sign_point`  (
  `student_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `sign_id` varchar(35) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `sign_point` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`student_id`, `sign_id`) USING BTREE,
  INDEX `sign_id`(`sign_id`) USING BTREE,
  CONSTRAINT `sign_id` FOREIGN KEY (`sign_id`) REFERENCES `sign_in` (`sign_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `student_id3` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for student_task_point
-- ----------------------------
DROP TABLE IF EXISTS `student_task_point`;
CREATE TABLE `student_task_point`  (
  `student_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `task_id` varchar(35) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `task_point` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`student_id`, `task_id`) USING BTREE,
  INDEX `taskid`(`task_id`) USING BTREE,
  CONSTRAINT `studnt_id4` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `taskid` FOREIGN KEY (`task_id`) REFERENCES `task` (`task_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for task
-- ----------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task`  (
  `task_id` varchar(35) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `task_title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `task_detail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `publish_time` datetime(0) NOT NULL,
  `publisher_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `task_end_time` datetime(0) NULL DEFAULT NULL,
  `status` int(11) NOT NULL,
  `task_category` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `course_id` varchar(35) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `use_num` int(11) NULL DEFAULT NULL COMMENT '使用次数',
  `watch_num` int(11) NULL DEFAULT NULL COMMENT '查看次数',
  `knowledge_points` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '知识点',
  PRIMARY KEY (`task_id`) USING BTREE,
  INDEX `task_category`(`task_category`) USING BTREE,
  CONSTRAINT `task_category` FOREIGN KEY (`task_category`) REFERENCES `point` (`task_category`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for teacher
-- ----------------------------
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher`  (
  `employee_num` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `teacher_name` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `teacher_password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `teacher_gender` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `education_background` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `professional_titles` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `telephone` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `email` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `face_img` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `teacher_nickname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `teacher_category` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `login_flag` int(255) NULL DEFAULT NULL COMMENT '0为未登录，1为登录',
  PRIMARY KEY (`employee_num`) USING BTREE,
  INDEX `teacher_category`(`teacher_category`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for teacher_course
-- ----------------------------
DROP TABLE IF EXISTS `teacher_course`;
CREATE TABLE `teacher_course`  (
  `employee_num` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `course_id` varchar(35) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `manager` int(1) NOT NULL COMMENT '0为课程加入者，1为课程创建者，2为关注者',
  PRIMARY KEY (`employee_num`, `course_id`, `manager`) USING BTREE,
  INDEX `course_id3`(`course_id`) USING BTREE,
  CONSTRAINT `course_id3` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `employee_num` FOREIGN KEY (`employee_num`) REFERENCES `teacher` (`employee_num`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for teacher_project
-- ----------------------------
DROP TABLE IF EXISTS `teacher_project`;
CREATE TABLE `teacher_project`  (
  `teacher_project_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `teacher_project_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `teahcer_project_detail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `declare_time` datetime(0) NULL DEFAULT NULL COMMENT '申报时间',
  `approval_time` datetime(0) NULL DEFAULT NULL COMMENT '审批时间',
  `finish_time` datetime(0) NULL DEFAULT NULL,
  `approval_organization` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审批部门',
  `fund` int(11) NULL DEFAULT NULL COMMENT '资助金额',
  `level` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '一般，重点',
  `teacher_project_category` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '教学改革，科学研究',
  PRIMARY KEY (`teacher_project_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for term
-- ----------------------------
DROP TABLE IF EXISTS `term`;
CREATE TABLE `term`  (
  `term_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `start_year` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `end_year` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `term` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`term_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for truancy
-- ----------------------------
DROP TABLE IF EXISTS `truancy`;
CREATE TABLE `truancy`  (
  `attendance_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `student_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '旷课人id',
  PRIMARY KEY (`attendance_id`, `student_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for up_accessory
-- ----------------------------
DROP TABLE IF EXISTS `up_accessory`;
CREATE TABLE `up_accessory`  (
  `up_accessory_id` int(11) NOT NULL AUTO_INCREMENT,
  `up_accessory_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `up_accessory_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `up_accessory_time` datetime(0) NULL DEFAULT NULL,
  `task_id` varchar(35) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `delete_flag` int(1) NULL DEFAULT NULL,
  `student_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`up_accessory_id`) USING BTREE,
  INDEX `task_id2`(`task_id`) USING BTREE,
  CONSTRAINT `task_id2` FOREIGN KEY (`task_id`) REFERENCES `task` (`task_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for up_task
-- ----------------------------
DROP TABLE IF EXISTS `up_task`;
CREATE TABLE `up_task`  (
  `student_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `task_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `up_task_detail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `grade` int(11) NULL DEFAULT NULL,
  `term` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `up_time` datetime(0) NULL DEFAULT NULL,
  `comment_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`student_id`, `task_id`) USING BTREE,
  INDEX `task_id3`(`task_id`) USING BTREE,
  CONSTRAINT `student_id5` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `task_id3` FOREIGN KEY (`task_id`) REFERENCES `task` (`task_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_attention_course
-- ----------------------------
DROP TABLE IF EXISTS `user_attention_course`;
CREATE TABLE `user_attention_course`  (
  `user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `course_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for virtual_class
-- ----------------------------
DROP TABLE IF EXISTS `virtual_class`;
CREATE TABLE `virtual_class`  (
  `virtual_class_num` varchar(35) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `virtual_class_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `creator_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `course_id` varchar(35) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `face_img` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `term` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `class_student_num` int(11) NULL DEFAULT NULL COMMENT '每个虚拟班的学生总数',
  `virtual_course_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `virtual_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`virtual_class_num`) USING BTREE,
  INDEX `course_id1`(`course_id`) USING BTREE,
  CONSTRAINT `course_id1` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
