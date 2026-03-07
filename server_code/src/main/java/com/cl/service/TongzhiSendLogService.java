package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.utils.PageUtils;
import com.cl.entity.TongzhiSendLogEntity;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.cl.entity.view.TongzhiSendLogView;


/**
 * 通知发送记录
 *
 * @author 
 * @email 
 * @date 2025-03-27
 */
public interface TongzhiSendLogService extends IService<TongzhiSendLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<TongzhiSendLogView> selectListView(Wrapper<TongzhiSendLogEntity> wrapper);
   	
   	TongzhiSendLogView selectView(@Param("ew") Wrapper<TongzhiSendLogEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<TongzhiSendLogEntity> wrapper);
   	
}
