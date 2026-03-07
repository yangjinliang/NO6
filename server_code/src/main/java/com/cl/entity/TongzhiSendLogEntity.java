package com.cl.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;
import com.baomidou.mybatisplus.enums.IdType;


/**
 * 通知发送记录
 * 数据库通用操作实体类
 * @author 
 * @email 
 * @date 2025-03-27
 */
@TableName("tongzhi_send_log")
public class TongzhiSendLogEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;


	public TongzhiSendLogEntity() {
		
	}
	
	public TongzhiSendLogEntity(T t) {
		try {
			BeanUtils.copyProperties(this, t);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * 通知ID
	 */
					
	private Long tongzhiId;
	
	/**
	 * 通知编号
	 */
					
	private String tongzhibianhao;
	
	/**
	 * 发送状态：0-发送中，1-发送成功，2-发送失败
	 */
					
	private Integer sendStatus;
	
	/**
	 * 发送时间
	 */
				
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat 		
	private Date sendTime;
	
	/**
	 * 发送结果信息
	 */
					
	private String sendResult;
	
	/**
	 * 接收人手机号
	 */
					
	private String shouji;
	
	/**
	 * 重试次数
	 */
					
	private Integer retryCount;


	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	private Date addtime;

	public Date getAddtime() {
		return addtime;
	}
	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 设置：通知ID
	 */
	public void setTongzhiId(Long tongzhiId) {
		this.tongzhiId = tongzhiId;
	}
	/**
	 * 获取：通知ID
	 */
	public Long getTongzhiId() {
		return tongzhiId;
	}
	/**
	 * 设置：通知编号
	 */
	public void setTongzhibianhao(String tongzhibianhao) {
		this.tongzhibianhao = tongzhibianhao;
	}
	/**
	 * 获取：通知编号
	 */
	public String getTongzhibianhao() {
		return tongzhibianhao;
	}
	/**
	 * 设置：发送状态
	 */
	public void setSendStatus(Integer sendStatus) {
		this.sendStatus = sendStatus;
	}
	/**
	 * 获取：发送状态
	 */
	public Integer getSendStatus() {
		return sendStatus;
	}
	/**
	 * 设置：发送时间
	 */
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	/**
	 * 获取：发送时间
	 */
	public Date getSendTime() {
		return sendTime;
	}
	/**
	 * 设置：发送结果信息
	 */
	public void setSendResult(String sendResult) {
		this.sendResult = sendResult;
	}
	/**
	 * 获取：发送结果信息
	 */
	public String getSendResult() {
		return sendResult;
	}
	/**
	 * 设置：接收人手机号
	 */
	public void setShouji(String shouji) {
		this.shouji = shouji;
	}
	/**
	 * 获取：接收人手机号
	 */
	public String getShouji() {
		return shouji;
	}
	/**
	 * 设置：重试次数
	 */
	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}
	/**
	 * 获取：重试次数
	 */
	public Integer getRetryCount() {
		return retryCount;
	}

}
