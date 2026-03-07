package com.cl.service;

import com.cl.entity.JiuzhentongzhiEntity;
import com.cl.entity.YishengyuyueEntity;
import java.util.List;

/**
 * 通知服务接口
 * 用于处理就诊通知的发送、重试等逻辑
 */
public interface NotificationService {
    
    /**
     * 预约审核通过后立即发送所有后续提醒
     * @param yuyue 医生预约实体
     * @return 是否发送成功
     */
    boolean sendImmediateNotifications(YishengyuyueEntity yuyue);
    
    /**
     * 发送单条通知
     * @param notification 通知实体
     * @return 是否发送成功
     */
    boolean sendNotification(JiuzhentongzhiEntity notification);
    
    /**
     * 重试发送失败的通知
     * @param notificationId 通知ID
     * @return 是否重试成功
     */
    boolean retryNotification(Long notificationId);
    
    /**
     * 批量重试发送失败的通知
     * @return 重试结果
     */
    int retryFailedNotifications();
    
    /**
     * 获取发送失败的通知列表
     * @return 失败通知列表
     */
    List<JiuzhentongzhiEntity> getFailedNotifications();
    
    /**
     * 更新用户接收状态
     * @param notificationId 通知ID
     * @param status 接收状态（0：未接收，1：已接收）
     * @return 是否更新成功
     */
    boolean updateReceiveStatus(Long notificationId, String status);
    
    /**
     * 创建预约相关通知
     * @param yuyue 医生预约实体
     * @return 创建的通知列表
     */
    List<JiuzhentongzhiEntity> createAppointmentNotifications(YishengyuyueEntity yuyue);
}
