package org.gwhere.service.impl;

import org.gwhere.mapper.SysInterfaceMapper;
import org.gwhere.mapper.SysResourceMapper;
import org.gwhere.mapper.SysUserTokenMapper;
import org.gwhere.model.SysInterface;
import org.gwhere.model.SysResource;
import org.gwhere.model.SysUserToken;
import org.gwhere.service.DataInitService;
import org.gwhere.utils.PermissionCache;
import org.gwhere.utils.TokenCache;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DataInitServiceImpl implements DataInitService {

    @Resource
    private SysResourceMapper sysResourceMapper;

    @Resource
    private SysInterfaceMapper sysInterfaceMapper;

    @Resource
    private SysUserTokenMapper sysUserTokenMapper;

    @Override
    public void initPermissionData() {
        if (PermissionCache.isNotInitialized()) {
            Example example = new Example(SysResource.class);
            example.createCriteria().andEqualTo("status", 1);
            List<SysResource> resources = sysResourceMapper.selectByExample(example);
            List<SysInterface> interfaces = sysInterfaceMapper.getEnableInterfaces();
            for (SysResource resource : resources) {
                PermissionCache.put(resource.getPath(), resource.generatePermissionVO());
            }
            for (SysInterface sysInterface : interfaces) {
                PermissionCache.put(sysInterface.getPath(), sysInterface.generatePermissionVO());
            }
        }
    }

    @Override
    public void initUserTokenData() {
        if (TokenCache.isNotInitialized()) {
            int pageSize = 1000;
            int start = 0;
            int resultSize;
            do {
                List<SysUserToken> list = sysUserTokenMapper.getEnableTokens(start, pageSize);
                for (SysUserToken userToken : list) {
                    TokenCache.put(String.valueOf(userToken.getUserId()), userToken.getToken());
                }
                start += list.size();
                resultSize = list.size();
            } while (resultSize == pageSize);
        }
    }
}
