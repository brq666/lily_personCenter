package org.gwhere.controller;

import org.gwhere.constant.Const;
import org.gwhere.database.PageHelper;
import org.gwhere.model.SysRole;
import org.gwhere.model.SysUser;
import org.gwhere.service.RoleService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource
    private RoleService roleService;

    /**
     * 获取角色
     *
     * @param roleCode
     * @param roleName
     * @return
     */
    @RequestMapping("/getRoles")
    public List<SysRole> getRoles(String roleCode, String roleName) {
        PageHelper.startPage();
        return roleService.getRoles(roleCode, roleName);
    }

    /**
     * 保存角色
     *
     * @param roles
     */
    @RequestMapping("/saveRoles")
    public void saveRoles(@RequestBody List<SysRole> roles, HttpSession session) {
        SysUser operator = (SysUser) session.getAttribute(Const.SESSION_USER);
        roleService.saveRoles(roles, operator);
    }
}
