package com.cl.service;

import com.cl.entity.JiuzhentongzhiEntity;
import com.cl.entity.TongzhiSendLogEntity;
import com.cl.service.TongzhiSendLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 通知发送服务
 */
@Service
public class NotificationSendService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationSendService.class);

    @Autowired
    private JiuzhentongzhiService jiuzhentongzhiService;

    @Autowired
    private TongzhiSendLogService tongzhiSendLogService;
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username:no-reply@hospital.com}")
    private String fromEmail;

    /**
     * 发送单个通知
     */
    @Async
    @Transactional
    public void sendNotification(JiuzhentongzhiEntity notification) {
        logger.info("开始发送通知，通知ID: {}, 编号: {}", notification.getId(), notification.getTongzhibianhao());
        
        // 更新通知状态为发送中
        notification.setSendStatus(1);
        notification.setLastSendTime(new Date());
        jiuzhentongzhiService.updateById(notification);
        
        // 创建发送记录
        TongzhiSendLogEntity sendLog = new TongzhiSendLogEntity();
        sendLog.setTongzhiId(notification.getId());
        sendLog.setTongzhibianhao(notification.getTongzhibianhao());
        sendLog.setSendTime(new Date());
        sendLog.setShouji(notification.getShouji());
        sendLog.setRetryCount(notification.getRetryCount() != null ? notification.getRetryCount() : 0);
        sendLog.setSendStatus(0); // 发送中
        tongzhiSendLogService.insert(sendLog);
        
        try {
            // 模拟发送通知（这里可以接入实际的短信/邮件/推送服务）
            boolean sendSuccess = doSendNotification(notification);
            
            if (sendSuccess) {
                // 发送成功
                notification.setSendStatus(2);
                notification.setSendResult("发送成功");
                sendLog.setSendStatus(1);
                sendLog.setSendResult("发送成功");
                logger.info("通知发送成功，通知ID: {}", notification.getId());
            } else {
                // 发送失败
                handleSendFailure(notification, sendLog, "发送失败");
            }
        } catch (Exception e) {
            logger.error("发送通知时发生异常，通知ID: {}", notification.getId(), e);
            handleSendFailure(notification, sendLog, "发送异常: " + e.getMessage());
        }
        
        // 更新通知和发送记录
        jiuzhentongzhiService.updateById(notification);
        tongzhiSendLogService.updateById(sendLog);
    }

    /**
     * 实际发送通知的逻辑
     * 发送邮件通知
     */
    private boolean doSendNotification(JiuzhentongzhiEntity notification) {
        try {
            if (mailSender == null) {
                logger.warn("邮件发送器未配置，使用模拟发送");
                return Math.random() > 0.3;
            }
            
            // 构建邮件内容
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String subject = "【医院就诊通知】" + notification.getTongzhibeizhu();
            StringBuilder content = new StringBuilder();
            content.append("尊敬的用户：\n\n");
            content.append("您好！\n\n");
            content.append("您有一条新的就诊通知：\n\n");
            content.append("通知类型：").append(notification.getTongzhibeizhu()).append("\n");
            content.append("就诊时间：").append(notification.getJiuzhenshijian() != null ? 
                sdf.format(notification.getJiuzhenshijian()) : "待定").append("\n");
            content.append("医生账号：").append(notification.getYishengzhanghao()).append("\n");
            if (notification.getTongzhibeizhu() != null) {
                content.append("备注：").append(notification.getTongzhibeizhu()).append("\n");
            }
            content.append("\n");
            content.append("请按时就诊，如有疑问请联系医院。\n\n");
            content.append("此致\n");
            content.append("医院管理系统");
            
            // 构建邮件消息
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            
            // 这里假设 shouji 字段存储的是邮箱地址
            // 如果 shouji 是手机号，您需要根据实际情况修改为获取邮箱的逻辑
            String toEmail = notification.getShouji();
            if (toEmail == null || !toEmail.contains("@")) {
                logger.warn("收件人邮箱地址无效: {}", toEmail);
                return false;
            }
            
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(content.toString());
            
            // 发送邮件
            mailSender.send(message);
            logger.info("邮件发送成功，收件人: {}, 主题: {}", toEmail, subject);
            return true;
            
        } catch (Exception e) {
            logger.error("邮件发送失败", e);
            return false;
        }
    }

    /**
     * 处理发送失败
     */
    private void handleSendFailure(JiuzhentongzhiEntity notification, TongzhiSendLogEntity sendLog, String errorMsg) {
        int retryCount = notification.getRetryCount() != null ? notification.getRetryCount() : 0;
        int maxRetryCount = notification.getMaxRetryCount() != null ? notification.getMaxRetryCount() : 3;
        
        retryCount++;
        notification.setRetryCount(retryCount);
        sendLog.setRetryCount(retryCount);
        sendLog.setSendStatus(2);
        sendLog.setSendResult(errorMsg);
        
        if (retryCount >= maxRetryCount) {
            // 达到最大重试次数，标记为失败
            notification.setSendStatus(3);
            notification.setSendResult(errorMsg + "，已达到最大重试次数");
            logger.warn("通知发送失败且已达最大重试次数，通知ID: {}", notification.getId());
        } else {
            // 还可以重试，保持待发送状态
            notification.setSendStatus(0);
            notification.setSendResult(errorMsg + "，将进行重试");
            logger.info("通知发送失败，将进行重试，通知ID: {}, 重试次数: {}/{}", 
                    notification.getId(), retryCount, maxRetryCount);
        }
    }

    /**
     * 批量重试失败的通知
     */
    @Transactional
    public void retryFailedNotifications() {
        logger.info("开始重试失败的通知...");
        
        // 查询待发送或失败且未达最大重试次数的通知
        List<JiuzhentongzhiEntity> notifications = jiuzhentongzhiService.selectList(
            new com.baomidou.mybatisplus.mapper.EntityWrapper<JiuzhentongzhiEntity>()
                .where("(send_status = 0 OR send_status = 3)")
                .and("(retry_count < max_retry_count OR max_retry_count IS NULL OR retry_count IS NULL)")
        );
        
        logger.info("找到 {} 个需要重试的通知", notifications.size());
        
        for (JiuzhentongzhiEntity notification : notifications) {
            if (notification.getMaxRetryCount() == null) {
                notification.setMaxRetryCount(3);
            }
            sendNotification(notification);
        }
        
        logger.info("通知重试完成");
    }

    /**
     * 手动重试单个通知
     */
    @Transactional
    public void retryNotificationById(Long notificationId) {
        JiuzhentongzhiEntity notification = jiuzhentongzhiService.selectById(notificationId);
        if (notification != null) {
            if (notification.getMaxRetryCount() == null) {
                notification.setMaxRetryCount(3);
            }
            // 重置重试次数
            if (notification.getRetryCount() == null) {
                notification.setRetryCount(0);
            }
            notification.setSendStatus(0);
            jiuzhentongzhiService.updateById(notification);
            sendNotification(notification);
        }
    }
}
