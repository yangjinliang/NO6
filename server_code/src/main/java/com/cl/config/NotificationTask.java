package com.cl.config;

import com.cl.service.NotificationSendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 通知定时任务
 */
@Configuration
@EnableScheduling
public class NotificationTask {

    private static final Logger logger = LoggerFactory.getLogger(NotificationTask.class);

    @Autowired
    private NotificationSendService notificationSendService;

    /**
     * 每5分钟执行一次，重试失败的通知
     */
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void retryFailedNotifications() {
        logger.info("定时任务开始：重试失败的通知");
        try {
            notificationSendService.retryFailedNotifications();
        } catch (Exception e) {
            logger.error("定时任务执行失败", e);
        }
    }
}
