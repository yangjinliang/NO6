-- 就诊通知表结构修改脚本
-- 添加通知状态、接收状态、重试机制等字段

USE `cl515882190`;

-- 添加新字段到就诊通知表
ALTER TABLE `jiuzhentongzhi` 
ADD COLUMN `tongzhizhuangtai` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT '0' COMMENT '通知状态（0：待发送，1：发送中，2：发送成功，3：发送失败）',
ADD COLUMN `jieshouzhuangtai` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT '0' COMMENT '用户接收状态（0：未接收，1：已接收）',
ADD COLUMN `retry_count` int(11) DEFAULT '0' COMMENT '重试次数',
ADD COLUMN `fail_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送失败原因',
ADD COLUMN `last_send_time` datetime DEFAULT NULL COMMENT '最后发送时间',
ADD COLUMN `receive_time` datetime DEFAULT NULL COMMENT '用户接收时间',
ADD COLUMN `yuyue_id` bigint(20) DEFAULT NULL COMMENT '预约ID（关联医生预约）';

-- 创建索引以提高查询性能
CREATE INDEX `idx_tongzhizhuangtai` ON `jiuzhentongzhi` (`tongzhizhuangtai`);
CREATE INDEX `idx_jieshouzhuangtai` ON `jiuzhentongzhi` (`jieshouzhuangtai`);
CREATE INDEX `idx_yuyue_id` ON `jiuzhentongzhi` (`yuyue_id`);
CREATE INDEX `idx_retry_count` ON `jiuzhentongzhi` (`retry_count`);

-- 更新现有数据的状态（如果有的话）
UPDATE `jiuzhentongzhi` SET 
    `tongzhizhuangtai` = '2',
    `jieshouzhuangtai` = '1'
WHERE `tongzhizhuangtai` IS NULL OR `tongzhizhuangtai` = '';
