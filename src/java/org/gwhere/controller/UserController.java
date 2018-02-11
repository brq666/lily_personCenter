package org.gwhere.controller;

import org.gwhere.constant.Const;
import org.gwhere.database.PageHelper;
import org.gwhere.model.SysUser;
import org.gwhere.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 后端登录
     *
     * @param username
     * @param password
     * @param session
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public SysUser login(String username, String password, HttpSession session)
            throws Exception {
        SysUser user = userService.execLogin(username, password);
        session.setAttribute(Const.SESSION_USER, user);
        return user;
    }

    /**
     * 后端退出
     *
     * @param session
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public void logout(HttpSession session) {
        session.removeAttribute(Const.SESSION_USER);
        session.invalidate();

    }

    /**
     * app端登录
     *
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(value = "/appLogin", method = RequestMethod.POST)
    public Object appLogin(String username, String password) {
        Object obj = userService.execAppLogin(username, password);
        return obj;
    }

    /**
     * app端退出
     *
     * @param userId
     */
    @RequestMapping(value = "/appLogout", method = RequestMethod.POST)
    public void appLogout(Long userId) {
        userService.execAppLogout(userId);
    }

    /**
     * app端自动登录
     *
     * @param path
     * @param sign
     * @param userId
     * @return
     */
    @RequestMapping(value = "/appAutoLogin", method = RequestMethod.POST)
    public Object appAutoLogin(String path, String sign, Long userId) {
        Object obj = userService.execAppAutoLogin(path, sign, userId);
        return obj;
    }

    /**
     * 获取用户
     *
     * @param username
     * @param phoneNumber
     * @param realName
     * @param nickname
     * @return
     */
    @RequestMapping("/getUsers")
    public List<SysUser> getUsers(String username, String phoneNumber, String realName, String nickname) {
        PageHelper.startPage();
        return userService.getUsers(username, phoneNumber, realName, nickname);
    }

    /**
     * 保存用户
     *
     * @param users
     */
    @RequestMapping("/saveUsers")
    public void saveUsers(@RequestBody List<SysUser> users, HttpSession session) {
        SysUser operator = (SysUser) session.getAttribute(Const.SESSION_USER);
        userService.saveUsers(users, operator);
    }


    /**
     * 获取可授权用户
     */
    @RequestMapping("/getGrantAbleUsers")
    public List<SysUser> getGrantAbleUsers() {
        return userService.getGrantAbleUsers();
    }
}
