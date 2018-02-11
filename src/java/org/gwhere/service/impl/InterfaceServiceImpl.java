package org.gwhere.service.impl;

import org.gwhere.exception.ErrorCode;
import org.gwhere.exception.SystemException;
import org.gwhere.mapper.SysInterfaceMapper;
import org.gwhere.model.SysInterface;
import org.gwhere.model.SysUser;
import org.gwhere.service.InterfaceService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class InterfaceServiceImpl implements InterfaceService {

    @Resource
    private SysInterfaceMapper sysInterfaceMapper;

    @Override
    public List<SysInterface> getInterfaces(String name, String path, String existIds) {
        Example example = new Example(SysInterface.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", 1);
        if (name != null && !"".equals(name)) {
            criteria.andEqualTo("name", name);
        }
        if (path != null && !"".equals(path)) {
            criteria.andLike("path", "%" + path + "%");
        }
        if (existIds != null && !"".equals(existIds)) {
            String[] idArr = existIds.split(",");
            List<Long> ids = new ArrayList<>();
            for (String id : idArr) {
                ids.add(Long.valueOf(id));
            }
            criteria.andNotIn("id", ids);
        }
        example.orderBy("name");
        return sysInterfaceMapper.selectByExample(example);
    }

    @Override
    public void saveInterfaces(List<SysInterface> interfaces, SysUser operator) {
        String operatorName = operator.getUsername();
        Date operateDate = new Date();
        for (SysInterface sysInterface : interfaces) {
            //脏数据
            if (sysInterface.getId() == null && sysInterface.getStatus() != 1) {
                continue;
            }

            validateInterface(sysInterface);

            if (sysInterface.getId() == null) {
                //新增数据
                sysInterface.setCreateTime(operateDate);
                sysInterface.setCreateUser(operatorName);
                sysInterface.setLastUpdateTime(operateDate);
                sysInterface.setLastUpdateUser(operatorName);
                sysInterfaceMapper.insertSelective(sysInterface);
            } else {
                //删除或修改
                sysInterface.setLastUpdateTime(operateDate);
                sysInterface.setLastUpdateUser(operatorName);
                sysInterfaceMapper.updateByPrimaryKeySelective(sysInterface);
            }
        }
    }

    private void validateInterface(SysInterface sysInterface) {

        //删除不做验证
        if (sysInterface.getStatus() == 0) {
            return;
        }
        if (sysInterface.getName() == null || "".equals(sysInterface.getName())) {
            throw new SystemException(ErrorCode.MODIFY_DATA_FAILED, "接口名称不能为空！");
        }
        if (sysInterface.getPath() == null || "".equals(sysInterface.getPath())) {
            throw new SystemException(ErrorCode.MODIFY_DATA_FAILED, "接口路径不能为空！");
        }
    }
}
