package com.cl.controller;

import java.util.*;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.annotation.SysLog;

import com.cl.entity.TongzhiSendLogEntity;
import com.cl.entity.view.TongzhiSendLogView;

import com.cl.service.TongzhiSendLogService;
import com.cl.utils.PageUtils;
import com.cl.utils.R;
import com.cl.utils.MPUtil;

/**
 * 通知发送记录
 * 后端接口
 * @author 
 * @email 
 * @date 2025-03-27
 */
@RestController
@RequestMapping("/tongzhisendlog")
public class TongzhiSendLogController {
    @Autowired
    private TongzhiSendLogService tongzhiSendLogService;


    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,TongzhiSendLogEntity tongzhisendlog,
    		HttpServletRequest request){
        EntityWrapper<TongzhiSendLogEntity> ew = new EntityWrapper<TongzhiSendLogEntity>();
		PageUtils page = tongzhiSendLogService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, tongzhisendlog), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,TongzhiSendLogEntity tongzhisendlog, 
		HttpServletRequest request){
        EntityWrapper<TongzhiSendLogEntity> ew = new EntityWrapper<TongzhiSendLogEntity>();
		PageUtils page = tongzhiSendLogService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, tongzhisendlog), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( TongzhiSendLogEntity tongzhisendlog){
       	EntityWrapper<TongzhiSendLogEntity> ew = new EntityWrapper<TongzhiSendLogEntity>();
      	ew.allEq(MPUtil.allEQMapPre( tongzhisendlog, "tongzhisendlog")); 
        return R.ok().put("data", tongzhiSendLogService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(TongzhiSendLogEntity tongzhisendlog){
        EntityWrapper< TongzhiSendLogEntity> ew = new EntityWrapper< TongzhiSendLogEntity>();
 		ew.allEq(MPUtil.allEQMapPre( tongzhisendlog, "tongzhisendlog")); 
		TongzhiSendLogView tongzhisendlogView =  tongzhiSendLogService.selectView(ew);
		return R.ok("查询通知发送记录成功").put("data", tongzhisendlogView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        TongzhiSendLogEntity tongzhisendlog = tongzhiSendLogService.selectById(id);
		tongzhisendlog = tongzhiSendLogService.selectView(new EntityWrapper<TongzhiSendLogEntity>().eq("id", id));
        return R.ok().put("data", tongzhisendlog);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        TongzhiSendLogEntity tongzhisendlog = tongzhiSendLogService.selectById(id);
		tongzhisendlog = tongzhiSendLogService.selectView(new EntityWrapper<TongzhiSendLogEntity>().eq("id", id));
        return R.ok().put("data", tongzhisendlog);
    }
    
    /**
     * 后端保存
     */
    @RequestMapping("/save")
    @SysLog("新增通知发送记录")
    public R save(@RequestBody TongzhiSendLogEntity tongzhisendlog, HttpServletRequest request){
    	tongzhiSendLogService.insert(tongzhisendlog);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @SysLog("新增通知发送记录")
    @RequestMapping("/add")
    public R add(@RequestBody TongzhiSendLogEntity tongzhisendlog, HttpServletRequest request){
        tongzhiSendLogService.insert(tongzhisendlog);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    @SysLog("修改通知发送记录")
    public R update(@RequestBody TongzhiSendLogEntity tongzhisendlog, HttpServletRequest request){
        tongzhiSendLogService.updateById(tongzhisendlog);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @SysLog("删除通知发送记录")
    public R delete(@RequestBody Long[] ids){
        tongzhiSendLogService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
	
}
