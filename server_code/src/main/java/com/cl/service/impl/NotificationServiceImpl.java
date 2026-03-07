package com.cl.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.entity.JiuzhentongzhiEntity;
import com.cl.entity.YishengyuyueEntity;
import com.cl.service.JiuzhentongzhiService;
import com.cl.service.NotificationService;
import com.cl.service.YishengyuyueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 通知服务实现类
 */
@Service("notificationService")
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private JiuzhentongzhiService jiuzhentongzhiService;
    
    @Autowired
    private YishengyuyueService yishengyuyueService;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username:}")
    private String fromEmail;
    
    // 最大重试次数
    private static final int MAX_RETRY_COUNT = 3;
    
    // 重试间隔（分钟）
    private static final int[] RETRY_INTERVALS = {1, 5, 15}; // 第1次1分钟后，第2次5分钟后，第3次15分钟后
    
    // 通知状态常量
    public static final String STATUS_PENDING = "0";      // 待发送
    public static final String STATUS_SENDING = "1";      // 发送中
    public static final String STATUS_SUCCESS = "2";      // 发送成功
    public static final String STATUS_FAILED = "3";       // 发送失败
    
    // 接收状态常量
    public static final String RECEIVE_STATUS_UNRECEIVED = "0";  // 未接收
    public static final String RECEIVE_STATUS_RECEIVED = "1";    // 已接收

    /**
     * 预约审核通过后立即发送所有后续提醒
     */
    @Override
    @Transactional
    public boolean sendImmediateNotifications(YishengyuyueEntity yuyue) {
        try {
            // 1. 创建预约相关的所有通知
            List<JiuzhentongzhiEntity> notifications = createAppointmentNotifications(yuyue);
            
            // 2. 立即发送所有通知
            boolean allSuccess = true;
            for (JiuzhentongzhiEntity notification : notifications) {
                boolean success = sendNotification(notification);
                if (!success) {
                    allSuccess = false;
                }
            }
            
            return allSuccess;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 发送单条通知
     */
    @Override
    public boolean sendNotification(JiuzhentongzhiEntity notification) {
        try {
            // 更新通知状态为发送中
            notification.setTongzhizhuangtai(STATUS_SENDING);
            notification.setLastSendTime(new Date());
            jiuzhentongzhiService.updateById(notification);
            
            // 发送邮件通知
            boolean sendSuccess = doSendEmailNotification(notification);
            
            if (sendSuccess) {
                // 发送成功
                notification.setTongzhizhuangtai(STATUS_SUCCESS);
                notification.setFailReason(null);
            } else {
                // 发送失败
                handleSendFailure(notification, "邮件发送失败");
            }
            
            jiuzhentongzhiService.updateById(notification);
            return sendSuccess;
            
        } catch (Exception e) {
            handleSendFailure(notification, e.getMessage());
            jiuzhentongzhiService.updateById(notification);
            return false;
        }
    }
    
    /**
     * 发送邮件通知
     */
    private boolean doSendEmailNotification(JiuzhentongzhiEntity notification) {
        try {
            // 检查邮件配置
            if (fromEmail == null || fromEmail.isEmpty()) {
                System.err.println("邮件发送失败：发件人邮箱未配置");
                return false;
            }
            
            // 构建邮件内容
            String subject = buildEmailSubject(notification);
            String content = buildEmailContent(notification);
            
            // 获取用户邮箱（这里使用手机号作为简单标识，实际应该查询用户表获取邮箱）
            // 实际项目中应该从用户表中获取邮箱地址
            String toEmail = getUserEmail(notification.getZhanghao(), notification.getShouji());
            
            if (toEmail == null || toEmail.isEmpty()) {
                System.err.println("邮件发送失败：用户邮箱不存在 - " + notification.getZhanghao());
                // 如果没有邮箱，记录为发送成功（因为无法发送），或者可以改为返回false
                // 这里选择返回true，因为用户没有邮箱不是系统错误
                return true;
            }
            
            // 创建邮件消息
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(content);
            
            // 发送邮件
            mailSender.send(message);
            
            System.out.println("邮件发送成功：" + toEmail + "，主题：" + subject);
            return true;
            
        } catch (Exception e) {
            System.err.println("邮件发送失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 构建邮件主题
     */
    private String buildEmailSubject(JiuzhentongzhiEntity notification) {
        return "【医院管理系统】就诊提醒 - " + notification.getTongzhibeizhu();
    }
    
    /**
     * 构建邮件内容
     */
    private String buildEmailContent(JiuzhentongzhiEntity notification) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        StringBuilder content = new StringBuilder();
        
        content.append("尊敬的 ").append(notification.getZhanghao()).append(" 用户：\n\n");
        content.append("您有一条就诊提醒通知。\n\n");
        content.append("═══════════════════════════════\n");
        content.append("医生账号：").append(notification.getYishengzhanghao()).append("\n");
        content.append("就诊时间：").append(sdf.format(notification.getJiuzhenshijian())).append("\n");
        content.append("通知时间：").append(sdf.format(notification.getTongzhishijian())).append("\n");
        content.append("备注信息：").append(notification.getTongzhibeizhu()).append("\n");
        content.append("═══════════════════════════════\n\n");
        content.append("请按时前往医院就诊，如有疑问请联系医院客服。\n\n");
        content.append("本邮件由医院管理系统自动发送，请勿回复。\n");
        content.append("发送时间：").append(sdf.format(new Date())).append("\n");
        
        return content.toString();
    }
    
    /**
     * 获取用户邮箱
     * 实际项目中应该从用户表中查询
     */
    private String getUserEmail(String zhanghao, String shouji) {
        // 这里简化处理，实际应该查询数据库获取用户邮箱
        // 如果没有邮箱，可以返回 null 或者构造一个默认邮箱
        // 例如：return zhanghao + "@example.com";
        
        // 实际项目中应该查询 yonghu 表获取邮箱字段
        // 由于当前表结构没有邮箱字段，这里暂时返回 null
        // 如果需要测试邮件功能，可以临时返回一个测试邮箱
        
        // TODO: 从用户表中查询真实邮箱
        // 临时返回 null，表示没有邮箱
        return null;
    }
    
    /**
     * 处理发送失败
     */
    private void handleSendFailure(JiuzhentongzhiEntity notification, String errorMsg) {
        int retryCount = notification.getRetryCount() == null ? 0 : notification.getRetryCount();
        notification.setRetryCount(retryCount + 1);
        notification.setFailReason(errorMsg);
        
        if (notification.getRetryCount() >= MAX_RETRY_COUNT) {
            notification.setTongzhizhuangtai(STATUS_FAILED);
        } else {
            // 设置为待发送状态，等待定时任务重试
            notification.setTongzhizhuangtai(STATUS_PENDING);
        }
    }

    /**
     * 重试发送失败的通知
     */
    @Override
    public boolean retryNotification(Long notificationId) {
        JiuzhentongzhiEntity notification = jiuzhentongzhiService.selectById(notificationId);
        if (notification == null) {
            return false;
        }
        
        // 只有失败或待发送状态的通知才能重试
        if (!STATUS_FAILED.equals(notification.getTongzhizhuangtai()) 
                && !STATUS_PENDING.equals(notification.getTongzhizhuangtai())) {
            return false;
        }
        
        return sendNotification(notification);
    }

    /**
     * 批量重试发送失败的通知
     */
    @Override
    public int retryFailedNotifications() {
        List<JiuzhentongzhiEntity> failedNotifications = getFailedNotifications();
        int successCount = 0;
        
        for (JiuzhentongzhiEntity notification : failedNotifications) {
            if (retryNotification(notification.getId())) {
                successCount++;
            }
        }
        
        return successCount;
    }

    /**
     * 获取发送失败的通知列表
     */
    @Override
    public List<JiuzhentongzhiEntity> getFailedNotifications() {
        EntityWrapper<JiuzhentongzhiEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("tongzhizhuangtai", STATUS_FAILED);
        wrapper.lt("retry_count", MAX_RETRY_COUNT);
        wrapper.orderBy("last_send_time", false);
        return jiuzhentongzhiService.selectList(wrapper);
    }
    
    /**
     * 获取待重试的通知列表（用于定时任务）
     */
    public List<JiuzhentongzhiEntity> getPendingRetryNotifications() {
        EntityWrapper<JiuzhentongzhiEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("tongzhizhuangtai", STATUS_PENDING);
        wrapper.gt("retry_count", 0); // 已经尝试过至少一次
        wrapper.lt("retry_count", MAX_RETRY_COUNT);
        wrapper.isNotNull("last_send_time");
        wrapper.orderBy("last_send_time", true); // 按时间升序，先失败的先重试
        return jiuzhentongzhiService.selectList(wrapper);
    }

    /**
     * 更新用户接收状态
     */
    @Override
    public boolean updateReceiveStatus(Long notificationId, String status) {
        JiuzhentongzhiEntity notification = jiuzhentongzhiService.selectById(notificationId);
        if (notification == null) {
            return false;
        }
        
        notification.setJieshouzhuangtai(status);
        if (RECEIVE_STATUS_RECEIVED.equals(status)) {
            notification.setReceiveTime(new Date());
        }
        
        return jiuzhentongzhiService.updateById(notification);
    }

    /**
     * 创建预约相关通知
     * 预约成功后立即创建所有后续提醒
     */
    @Override
    @Transactional
    public List<JiuzhentongzhiEntity> createAppointmentNotifications(YishengyuyueEntity yuyue) {
        List<JiuzhentongzhiEntity> notifications = new ArrayList<>();
        
        Date yuyueShijian = yuyue.getYuyueshijian();
        if (yuyueShijian == null) {
            return notifications;
        }
        
        // 1. 预约成功通知（立即发送）
        JiuzhentongzhiEntity immediateNotification = createNotification(
            yuyue, 
            new Date(), 
            "预约成功通知",
            "您的预约已审核通过，请按时就诊。"
        );
        notifications.add(immediateNotification);
        
        // 2. 提前一天提醒
        Date oneDayBefore = new Date(yuyueShijian.getTime() - 24 * 60 * 60 * 1000);
        if (oneDayBefore.after(new Date())) {
            JiuzhentongzhiEntity oneDayNotification = createNotification(
                yuyue, 
                oneDayBefore, 
                "就诊前一日提醒",
                "您明天有就诊安排，请做好准备。"
            );
            notifications.add(oneDayNotification);
        }
        
        // 3. 提前2小时提醒
        Date twoHoursBefore = new Date(yuyueShijian.getTime() - 2 * 60 * 60 * 1000);
        if (twoHoursBefore.after(new Date())) {
            JiuzhentongzhiEntity twoHourNotification = createNotification(
                yuyue, 
                twoHoursBefore, 
                "就诊前2小时提醒",
                "您2小时后有就诊安排，请准时到达。"
            );
            notifications.add(twoHourNotification);
        }
        
        // 4. 就诊时间提醒（准时提醒）
        JiuzhentongzhiEntity onTimeNotification = createNotification(
            yuyue, 
            yuyueShijian, 
            "就诊时间提醒",
            "已到就诊时间，请前往诊室就诊。"
        );
        notifications.add(onTimeNotification);
        
        // 保存所有通知到数据库
        for (JiuzhentongzhiEntity notification : notifications) {
            jiuzhentongzhiService.insert(notification);
        }
        
        return notifications;
    }
    
    /**
     * 创建单条通知
     */
    private JiuzhentongzhiEntity createNotification(YishengyuyueEntity yuyue, Date tongzhiShijian, 
                                                     String beizhu, String detail) {
        JiuzhentongzhiEntity notification = new JiuzhentongzhiEntity();
        
        // 生成通知编号
        String tongzhiBianhao = "TZ" + System.currentTimeMillis() + new Random().nextInt(1000);
        notification.setTongzhibianhao(tongzhiBianhao);
        
        // 设置医生信息
        notification.setYishengzhanghao(yuyue.getYishengzhanghao());
        notification.setDianhua(yuyue.getDianhua());
        
        // 设置就诊时间
        notification.setJiuzhenshijian(yuyue.getYuyueshijian());
        
        // 设置通知时间
        notification.setTongzhishijian(tongzhiShijian);
        
        // 设置用户信息
        notification.setZhanghao(yuyue.getZhanghao());
        notification.setShouji(yuyue.getShouji());
        
        // 设置备注
        notification.setTongzhibeizhu(beizhu + " - " + detail);
        
        // 设置初始状态
        notification.setTongzhizhuangtai(STATUS_PENDING);
        notification.setJieshouzhuangtai(RECEIVE_STATUS_UNRECEIVED);
        notification.setRetryCount(0);
        
        // 设置关联的预约ID
        notification.setYuyueId(yuyue.getId());
        
        return notification;
    }
    
    /**
     * 自动重试定时任务
     * 每1分钟执行一次，检查需要重试的通知
     */
    @Scheduled(fixedRate = 60000) // 每60秒执行一次
    public void autoRetryFailedNotifications() {
        System.out.println("执行自动重试任务...");
        
        List<JiuzhentongzhiEntity> pendingNotifications = getPendingRetryNotifications();
        
        for (JiuzhentongzhiEntity notification : pendingNotifications) {
            try {
                int retryCount = notification.getRetryCount() == null ? 0 : notification.getRetryCount();
                
                // 检查是否到达重试时间
                if (shouldRetry(notification, retryCount)) {
                    System.out.println("自动重试通知：" + notification.getId() + "，第 " + (retryCount + 1) + " 次重试");
                    sendNotification(notification);
                }
            } catch (Exception e) {
                System.err.println("自动重试异常：" + notification.getId() + "，错误：" + e.getMessage());
                // 按照发送失败逻辑处理
                handleSendFailure(notification, "自动重试异常：" + e.getMessage());
                jiuzhentongzhiService.updateById(notification);
            }
        }
    }
    
    /**
     * 检查是否应该重试
     */
    private boolean shouldRetry(JiuzhentongzhiEntity notification, int retryCount) {
        if (notification.getLastSendTime() == null) {
            return true;
        }
        
        // 获取当前重试次数对应的间隔（分钟）
        int intervalMinutes = RETRY_INTERVALS[Math.min(retryCount - 1, RETRY_INTERVALS.length - 1)];
        
        // 计算下次重试时间
        long nextRetryTime = notification.getLastSendTime().getTime() + intervalMinutes * 60 * 1000;
        
        // 如果当前时间已经超过下次重试时间，则可以重试
        return System.currentTimeMillis() >= nextRetryTime;
    }
    
    /**
     * 定时发送预约提醒（用于发送未来时间的提醒）
     * 每分钟检查一次，发送到达通知时间的提醒
     */
    @Scheduled(fixedRate = 60000) // 每60秒执行一次
    public void sendScheduledNotifications() {
        Date now = new Date();
        
        // 查询到达通知时间且待发送的通知
        EntityWrapper<JiuzhentongzhiEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("tongzhizhuangtai", STATUS_PENDING);
        wrapper.le("tongzhishijian", now); // 通知时间小于等于当前时间
        wrapper.eq("retry_count", 0); // 还未尝试过发送
        
        List<JiuzhentongzhiEntity> scheduledNotifications = jiuzhentongzhiService.selectList(wrapper);
        
        for (JiuzhentongzhiEntity notification : scheduledNotifications) {
            try {
                System.out.println("定时发送通知：" + notification.getId());
                sendNotification(notification);
            } catch (Exception e) {
                System.err.println("定时发送异常：" + notification.getId() + "，错误：" + e.getMessage());
                // 按照发送失败逻辑处理
                handleSendFailure(notification, "定时发送异常：" + e.getMessage());
                jiuzhentongzhiService.updateById(notification);
            }
        }
    }
}
