package org.gwhere.service;

import org.gwhere.model.SysUser;

import java.util.List;

public interface UserService {

    /**
     * 后端登录
     *
     * @param username
     * @param password
     * @return
     */
    SysUser execLogin(String username, String password);

    /**
     * APP端登录
     *
     * @param username
     * @param password
     */
    Object execAppLogin(String username, String password);

    /**
     * APP端退出
     *
     * @param userId
     */
    void execAppLogout(Long userId);

    /**
     * APP端自动登录
     *
     * @param path
     * @param sign
     * @param userId
     */
    Object execAppAutoLogin(String path, String sign, Long userId);

    /**
     * 获取用户
     *
     * @param username
     * @param phoneNumber
     * @param realName
     * @param nickname
     * @return
     */
    List<SysUser> getUsers(String username, String phoneNumber, String realName, String nickname);

    /**
     * 保存用户
     *
     * @param users
     * @param operator
     */
    void saveUsers(List<SysUser> users, SysUser operator);

    /**
     * 获取可授权用户
     * @return
     */
    List<SysUser> getGrantAbleUsers();
}
