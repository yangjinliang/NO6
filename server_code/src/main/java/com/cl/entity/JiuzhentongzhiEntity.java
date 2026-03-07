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
 * 就诊通知
 * 数据库通用操作实体类（普通增删改查）
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
@TableName("jiuzhentongzhi")
public class JiuzhentongzhiEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;


	public JiuzhentongzhiEntity() {
		
	}
	
	public JiuzhentongzhiEntity(T t) {
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
	 * 通知编号
	 */
					
	private String tongzhibianhao;
	
	/**
	 * 医生账号
	 */
					
	private String yishengzhanghao;
	
	/**
	 * 电话
	 */
					
	private String dianhua;
	
	/**
	 * 就诊时间
	 */
				
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat 		
	private Date jiuzhenshijian;
	
	/**
	 * 通知时间
	 */
				
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat 		
	private Date tongzhishijian;
	
	/**
	 * 账号
	 */
					
	private String zhanghao;
	
	/**
	 * 手机
	 */
					
	private String shouji;
	
	/**
	 * 通知备注
	 */
					
	private String tongzhibeizhu;
	
	/**
	 * 发送状态：0-待发送，1-发送中，2-发送成功，3-发送失败
	 */
					
	private Integer sendStatus;
	
	/**
	 * 重试次数
	 */
					
	private Integer retryCount;
	
	/**
	 * 最大重试次数
	 */
					
	private Integer maxRetryCount;
	
	/**
	 * 最后发送时间
	 */
				
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat 		
	private Date lastSendTime;
	
	/**
	 * 发送结果信息
	 */
					
	private String sendResult;


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
	 * 设置：医生账号
	 */
	public void setYishengzhanghao(String yishengzhanghao) {
		this.yishengzhanghao = yishengzhanghao;
	}
	/**
	 * 获取：医生账号
	 */
	public String getYishengzhanghao() {
		return yishengzhanghao;
	}
	/**
	 * 设置：电话
	 */
	public void setDianhua(String dianhua) {
		this.dianhua = dianhua;
	}
	/**
	 * 获取：电话
	 */
	public String getDianhua() {
		return dianhua;
	}
	/**
	 * 设置：就诊时间
	 */
	public void setJiuzhenshijian(Date jiuzhenshijian) {
		this.jiuzhenshijian = jiuzhenshijian;
	}
	/**
	 * 获取：就诊时间
	 */
	public Date getJiuzhenshijian() {
		return jiuzhenshijian;
	}
	/**
	 * 设置：通知时间
	 */
	public void setTongzhishijian(Date tongzhishijian) {
		this.tongzhishijian = tongzhishijian;
	}
	/**
	 * 获取：通知时间
	 */
	public Date getTongzhishijian() {
		return tongzhishijian;
	}
	/**
	 * 设置：账号
	 */
	public void setZhanghao(String zhanghao) {
		this.zhanghao = zhanghao;
	}
	/**
	 * 获取：账号
	 */
	public String getZhanghao() {
		return zhanghao;
	}
	/**
	 * 设置：手机
	 */
	public void setShouji(String shouji) {
		this.shouji = shouji;
	}
	/**
	 * 获取：手机
	 */
	public String getShouji() {
		return shouji;
	}
	/**
	 * 设置：通知备注
	 */
	public void setTongzhibeizhu(String tongzhibeizhu) {
		this.tongzhibeizhu = tongzhibeizhu;
	}
	/**
	 * 获取：通知备注
	 */
	public String getTongzhibeizhu() {
		return tongzhibeizhu;
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
	/**
	 * 设置：最大重试次数
	 */
	public void setMaxRetryCount(Integer maxRetryCount) {
		this.maxRetryCount = maxRetryCount;
	}
	/**
	 * 获取：最大重试次数
	 */
	public Integer getMaxRetryCount() {
		return maxRetryCount;
	}
	/**
	 * 设置：最后发送时间
	 */
	public void setLastSendTime(Date lastSendTime) {
		this.lastSendTime = lastSendTime;
	}
	/**
	 * 获取：最后发送时间
	 */
	public Date getLastSendTime() {
		return lastSendTime;
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

}
