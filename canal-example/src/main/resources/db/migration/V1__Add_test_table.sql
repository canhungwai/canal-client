DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_name` varchar(255)  NOT NULL DEFAULT '' COMMENT '用户名',
  `gender` tinyint(4) NOT NULL DEFAULT 0 COMMENT '性别（1：男；2：女；0：未知）',
  `country_id` int(11) NOT NULL DEFAULT 0 COMMENT '国家id',
  `birthday` date  NULL DEFAULT NULL COMMENT '出生日期',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COMMENT '用户';
