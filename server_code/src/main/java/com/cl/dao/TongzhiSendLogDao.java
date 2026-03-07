package com.cl.dao;

import com.cl.entity.TongzhiSendLogEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.cl.entity.view.TongzhiSendLogView;


/**
 * 通知发送记录
 * 
 * @author 
 * @email 
 * @date 2025-03-27
 */
public interface TongzhiSendLogDao extends BaseMapper<TongzhiSendLogEntity> {
	
	List<TongzhiSendLogView> selectListView(@Param("ew") Wrapper<TongzhiSendLogEntity> wrapper);

	List<TongzhiSendLogView> selectListView(Pagination page,@Param("ew") Wrapper<TongzhiSendLogEntity> wrapper);
	
	TongzhiSendLogView selectView(@Param("ew") Wrapper<TongzhiSendLogEntity> wrapper);
	

}
