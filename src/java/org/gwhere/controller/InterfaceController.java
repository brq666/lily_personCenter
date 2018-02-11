package org.gwhere.controller;

import org.gwhere.constant.Const;
import org.gwhere.database.PageHelper;
import org.gwhere.model.SysInterface;
import org.gwhere.model.SysUser;
import org.gwhere.service.InterfaceService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/interface")
public class InterfaceController {

    @Resource
    private InterfaceService interfaceService;

    /**
     * 获取接口
     *
     * @param name
     * @param path
     * @param existIds
     * @return
     */
    @RequestMapping("/getInterfaces")
    public List<SysInterface> getInterfaces(String name, String path, String existIds) {
        PageHelper.startPage();
        return interfaceService.getInterfaces(name, path, existIds);
    }

    /**
     * 保存接口
     *
     * @param interfaces
     * @param session
     */
    @RequestMapping("/saveInterfaces")
    public void saveInterfaces(@RequestBody List<SysInterface> interfaces, HttpSession session) {
        SysUser operator = (SysUser) session.getAttribute(Const.SESSION_USER);
        interfaceService.saveInterfaces(interfaces, operator);
    }


}
