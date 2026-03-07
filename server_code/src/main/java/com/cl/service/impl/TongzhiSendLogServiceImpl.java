package com.cl.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.utils.PageUtils;
import com.cl.utils.Query;


import com.cl.dao.TongzhiSendLogDao;
import com.cl.entity.TongzhiSendLogEntity;
import com.cl.service.TongzhiSendLogService;
import com.cl.entity.view.TongzhiSendLogView;

@Service("tongzhiSendLogService")
public class TongzhiSendLogServiceImpl extends ServiceImpl<TongzhiSendLogDao, TongzhiSendLogEntity> implements TongzhiSendLogService {
	
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<TongzhiSendLogEntity> page = this.selectPage(
                new Query<TongzhiSendLogEntity>(params).getPage(),
                new EntityWrapper<TongzhiSendLogEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<TongzhiSendLogEntity> wrapper) {
		  Page<TongzhiSendLogView> page =new Query<TongzhiSendLogView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
	@Override
	public List<TongzhiSendLogView> selectListView(Wrapper<TongzhiSendLogEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public TongzhiSendLogView selectView(Wrapper<TongzhiSendLogEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}


}
