package org.gwhere.service.impl;

import org.gwhere.mapper.SysRequestLogMapper;
import org.gwhere.mapper.SysUserActionMapper;
import org.gwhere.model.SysRequestLog;
import org.gwhere.model.SysUser;
import org.gwhere.model.SysUserAction;
import org.gwhere.service.ActionService;
import org.gwhere.utils.Converter;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by sgq on 2017/7/24.
 */
@Service
public class ActionServiceImpl implements ActionService {
    @Resource
    private SysUserActionMapper sysUserActionMapper;

    @Override
    public void saveUserAction(List<SysUserAction> actions, SysUser user) {
        if(!CollectionUtils.isEmpty(actions)){
            Date date = new Date();
            for(SysUserAction action : actions){
                action.setStatus(1);
                action.setCreateUser("system");
                action.setLastUpdateUser("system");
                action.setCreateTime(date);
                action.setLastUpdateTime(date);
                sysUserActionMapper.insertSelective(action);
            }
        }
    }
}
