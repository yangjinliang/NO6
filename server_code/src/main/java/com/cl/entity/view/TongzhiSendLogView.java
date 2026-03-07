package com.cl.entity.view;

import com.cl.entity.TongzhiSendLogEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.BeanUtils;


/**
 * 通知发送记录
 * 后端返回视图实体辅助类
 * （通常后端关联的表或者自定义的字段需要返回使用）
 * @author
 * @email
 * @date 2025-03-27
 */
@TableName("tongzhi_send_log")
public class TongzhiSendLogView  extends TongzhiSendLogEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public TongzhiSendLogView(){
    }

    public TongzhiSendLogView(TongzhiSendLogEntity tongzhiSendLogEntity){
        try {
            BeanUtils.copyProperties(this, tongzhiSendLogEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}