
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin_user
-- ----------------------------
DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user` (
  `id` varchar(24) NOT NULL,
  `username` varchar(32) NOT NULL COMMENT '用户名',
  `password` varchar(64) DEFAULT NULL COMMENT '密码',
  `face_id` varchar(64) DEFAULT NULL COMMENT '人脸入库图片信息，该信息保存到mongoDB的gridFS中',
  `admin_name` varchar(12) DEFAULT NULL COMMENT '管理人员的姓名',
  `created_time` datetime NOT NULL COMMENT '创建时间 创建时间',
  `updated_time` datetime NOT NULL COMMENT '更新时间 更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运营管理平台的admin级别用户';

-- ----------------------------
-- Records of admin_user
-- ----------------------------
BEGIN;
INSERT INTO `admin_user` VALUES ('1001', 'admin', '$2a$10$c8Eh/a7NAwtOLOs/S4Kd9ufGaTFzBszfOuaRDhc09nl59HQWBNjpG', NULL, 'admin', '2020-07-09 11:33:39', '2020-07-09 11:33:44');
COMMIT;

-- ----------------------------
-- Table structure for app_user
-- ----------------------------
DROP TABLE IF EXISTS `app_user`;
CREATE TABLE `app_user` (
  `id` varchar(24) NOT NULL,
  `mobile` varchar(32) NOT NULL COMMENT '手机号',
  `nickname` varchar(16) NOT NULL COMMENT '昵称，媒体号',
  `face` varchar(128) NOT NULL COMMENT '头像',
  `realname` varchar(128) DEFAULT NULL COMMENT '真实姓名',
  `email` varchar(32) DEFAULT NULL COMMENT '邮箱地址',
  `sex` int(11) DEFAULT NULL COMMENT '性别 1:男  0:女  2:保密',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `province` varchar(32) DEFAULT NULL COMMENT '省份',
  `city` varchar(32) DEFAULT NULL COMMENT '城市',
  `district` varchar(32) DEFAULT NULL COMMENT '区县',
  `active_status` int(255) NOT NULL DEFAULT 0 COMMENT '用户状态：0：未激活。 1：已激活：基本信息是否完善，真实姓名，邮箱地址，性别，生日，住址等，如果没有完善，则用户不能在作家中心操作，不能关注。2：已冻结。',
  `total_income` int(255) NOT NULL DEFAULT 0 COMMENT '累计已结算的收入金额，也就是已经打款的金额，每次打款后再此累加',
  `created_time` datetime NOT NULL COMMENT '创建时间 创建时间',
  `updated_time` datetime NOT NULL COMMENT '更新时间 更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网站用户';


-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
  `id` varchar(24) NOT NULL,
  `title` varchar(64) NOT NULL COMMENT '文章标题',
  `content` longtext NOT NULL COMMENT '文章内容，长度不超过9999，需要在前后端判断',
  `category_id` int(2) NOT NULL COMMENT '文章所属分类id',
  `article_type` int(1) NOT NULL COMMENT '文章类型，1：图文（1张封面），2：纯文字',
  `article_cover` varchar(256) DEFAULT NULL COMMENT '文章封面图，article_type=1 的时候展示',
  `is_appoint` int(1) NOT NULL COMMENT '是否是预约定时发布的文章，1：预约（定时）发布，0：即时发布    在预约时间到点的时候，把1改为0，则发布',
  `article_status` int(1) NOT NULL COMMENT '文章状态，1：审核中（用户已提交），2：机审结束，等待人工审核，3：审核通过（已发布），4：审核未通过；5：文章撤回（已发布的情况下才能撤回和删除）',
  `publish_user_id` varchar(24) NOT NULL COMMENT '发布者用户id',
  `publish_time` datetime NOT NULL COMMENT '文章发布时间（也是预约发布的时间）',
  `read_counts` int(11) NOT NULL DEFAULT 0 COMMENT '用户累计点击阅读数（喜欢数）（点赞） - 放redis',
  `comment_counts` int(11) NOT NULL DEFAULT 0 COMMENT '文章评论总数。评论防刷，距离上次评论需要间隔时间控制几秒',
  `mongo_file_id` varchar(64) DEFAULT NULL,
  `is_delete` int(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除状态，非物理删除，1：删除，0：未删除',
  `create_time` datetime NOT NULL COMMENT '文章的创建时间',
  `update_time` datetime NOT NULL COMMENT '文章的修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章资讯表';

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` int(2) NOT NULL AUTO_INCREMENT,
  `name` varchar(12) NOT NULL COMMENT '分类名，比如：技术，工具',
  `tag_color` varchar(12) NOT NULL COMMENT '标签颜色',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COMMENT='新闻资讯文章的分类（或者称之为领域）';

-- ----------------------------
-- Records of category
-- ----------------------------
BEGIN;
INSERT INTO `category` VALUES (2, '技术', '#8939bd');
INSERT INTO `category` VALUES (3, '工具', '#c939aa');
COMMIT;

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments` (
  `id` varchar(24) NOT NULL,
  `writer_id` varchar(24) NOT NULL COMMENT '评论的文章是哪个作者的关联id',
  `father_id` varchar(24) NOT NULL COMMENT '如果是回复留言，则本条为子留言，需要关联查询',
  `article_id` varchar(24) NOT NULL COMMENT '回复的那个文章id',
  `article_title` varchar(64) NOT NULL COMMENT '冗余文章标题，宽表处理，非规范化的sql思维，对于几百万文章和几百万评论的关联查询来讲，性能肯定不行，所以做宽表处理，从业务角度来说，文章发布以后不能随便修改标题和封面的',
  `article_cover` varchar(128) DEFAULT NULL COMMENT '文章封面',
  `comment_user_id` varchar(24) NOT NULL COMMENT '发布留言的用户id',
  `comment_user_nickname` varchar(16) NOT NULL COMMENT '冗余用户昵称，非一致性字段，用户修改昵称后可以不用同步',
  `comment_user_face` varchar(128) NOT NULL COMMENT '冗余的用户头像',
  `content` varchar(128) NOT NULL COMMENT '留言内容',
  `create_time` datetime NOT NULL COMMENT '留言时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章评论表';


-- ----------------------------
-- Table structure for fans
-- ----------------------------
DROP TABLE IF EXISTS `fans`;
CREATE TABLE `fans` (
  `id` varchar(24) NOT NULL,
  `writer_id` varchar(24) NOT NULL COMMENT '作家用户id',
  `fan_id` varchar(24) NOT NULL COMMENT '粉丝用户id',
  `face` varchar(128) DEFAULT NULL COMMENT '粉丝头像',
  `fan_nickname` varchar(16) NOT NULL COMMENT '粉丝昵称',
  `sex` int(11) NOT NULL COMMENT '粉丝性别',
  `province` varchar(16) NOT NULL COMMENT '省份',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `writer_id` (`writer_id`,`fan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='粉丝表，用户与粉丝的关联关系，粉丝本质也是用户。\n关联关系保存到es中，粉丝数方式和用户点赞收藏文章一样。累加累减都用redis来做。\n字段与用户表有些冗余，主要用于数据可视化，数据一旦有了之后，用户修改性别和省份无法影响此表，只认第一次的数据。\n\n';



SET FOREIGN_KEY_CHECKS = 1;
