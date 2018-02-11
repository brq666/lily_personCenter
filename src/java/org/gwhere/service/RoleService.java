package org.gwhere.service;

import org.gwhere.model.SysRole;
import org.gwhere.model.SysUser;

import java.util.List;

public interface RoleService {

    /**
     * 获取角色
     *
     * @param roleCode
     * @param roleName
     * @return
     */
    List<SysRole> getRoles(String roleCode, String roleName);

    /**
     * 保存角色
     *
     * @param roles
     */
    void saveRoles(List<SysRole> roles, SysUser operator);
}
