-- 医院管理系统通知功能升级脚本
-- 执行日期: 2025-03-27

-- 1. 为 jiuzhentongzhi 表添加新字段
ALTER TABLE `jiuzhentongzhi` 
ADD COLUMN `send_status` INT(11) DEFAULT 0 COMMENT '发送状态：0-待发送，1-发送中，2-发送成功，3-发送失败',
ADD COLUMN `retry_count` INT(11) DEFAULT 0 COMMENT '重试次数',
ADD COLUMN `max_retry_count` INT(11) DEFAULT 3 COMMENT '最大重试次数',
ADD COLUMN `last_send_time` DATETIME DEFAULT NULL COMMENT '最后发送时间',
ADD COLUMN `send_result` VARCHAR(500) DEFAULT NULL COMMENT '发送结果信息';

-- 2. 创建通知发送记录表
DROP TABLE IF EXISTS `tongzhi_send_log`;
CREATE TABLE `tongzhi_send_log` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tongzhi_id` BIGINT(20) NOT NULL COMMENT '通知ID',
  `tongzhibianhao` VARCHAR(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通知编号',
  `send_status` INT(11) DEFAULT 0 COMMENT '发送状态：0-发送中，1-发送成功，2-发送失败',
  `send_time` DATETIME DEFAULT NULL COMMENT '发送时间',
  `send_result` VARCHAR(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送结果信息',
  `shouji` VARCHAR(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接收人手机号',
  `retry_count` INT(11) DEFAULT 0 COMMENT '重试次数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知发送记录表';

-- 3. 更新现有数据的默认值
UPDATE `jiuzhentongzhi` SET `send_status` = 2, `retry_count` = 0, `max_retry_count` = 3, `send_result` = '已发送' WHERE `send_status` IS NULL;
