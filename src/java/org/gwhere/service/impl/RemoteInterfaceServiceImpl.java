package org.gwhere.service.impl;

import org.gwhere.mapper.SysRequestLogMapper;
import org.gwhere.model.SysRequestLog;
import org.gwhere.model.SysUser;
import org.gwhere.service.RemoteInterfaceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by sgq on 2017/7/24.
 */
@Service
public class RemoteInterfaceServiceImpl implements RemoteInterfaceService {
    @Resource
    private SysRequestLogMapper sysRequestLogMapper;

    @Override
    public void saveRequestLog(SysRequestLog log, SysUser user){
        Date date = new Date();
        log.setCreateTime(date);
        log.setLastUpdateTime(date);
        log.setStatus(1);
        log.setCreateUser("system");
        log.setLastUpdateUser("system");
        sysRequestLogMapper.insertSelective(log);
    }
}
