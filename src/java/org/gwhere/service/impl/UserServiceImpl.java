package org.gwhere.service.impl;

import org.gwhere.exception.ErrorCode;
import org.gwhere.exception.SystemException;
import org.gwhere.mapper.*;
import org.gwhere.model.*;
import org.gwhere.service.UserService;
import org.gwhere.utils.Digests;
import org.gwhere.utils.Encodes;
import org.gwhere.utils.PasswordHelper;
import org.gwhere.utils.TokenCache;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private SysResourceMapper sysResourceMapper;

    @Resource
    private SysInterfaceMapper sysInterfaceMapper;

    @Resource
    private SysUserTokenMapper sysUserTokenMapper;

    @Override
    public SysUser execLogin(String username, String password) {
        Example example = new Example(SysUser.class);
        example.createCriteria().andEqualTo("status", 1)
                .andEqualTo("username", username);
        List<SysUser> users = sysUserMapper.selectByExample(example);
        if (users == null || users.isEmpty()) {
            throw new SystemException(ErrorCode.UNKNOWN, "用户名或密码错误！");
        }
        SysUser user = users.get(0);
        String salt = user.getSalt();

        if (!PasswordHelper.generatePassword(password, salt).equals(user.getPassword())) {
            throw new SystemException(ErrorCode.UNKNOWN, "用户名或密码错误！");
        }

        List<SysRole> roles = sysRoleMapper.getRolesByUserId(user.getId());
        user.setRoles(roles);

        user.setLastLoginTime(new Date());
        sysUserMapper.updateByPrimaryKey(user);

        List<String> resourcePaths = sysResourceMapper.getResourcePathsByUserId(user.getId());
        List<String> interfacePaths = sysInterfaceMapper.getInterfacePathsByUserId(user.getId());
        user.getPermissions().addAll(resourcePaths);
        user.getPermissions().addAll(interfacePaths);
        return user;
    }

    @Override
    public Object execAppLogin(String username, String password) {

        Example userExample = new Example(SysUser.class);
        userExample.createCriteria().andEqualTo("username", username)
                .andEqualTo("status", 1);
        List<SysUser> users = sysUserMapper.selectByExample(userExample);

        if (users == null || users.isEmpty()) {
            throw new SystemException(ErrorCode.UNKNOWN, "用户不存在");
        }

        SysUser user = users.get(0);

        String salt = user.getSalt();
        if (!PasswordHelper.generatePassword(password, salt).equals(user.getPassword())) {
            throw new SystemException(ErrorCode.UNKNOWN, "密码错误");
        }

        Date nowDate = new Date();
        user.setLastLoginTime(nowDate);
        sysUserMapper.updateByPrimaryKey(user);

        Long userId = user.getId();
        String strUserId = String.valueOf(userId);
        String timeStr = String.valueOf(System.currentTimeMillis());

        sysUserTokenMapper.disableUserToken(userId, nowDate);
        TokenCache.clean(strUserId);

        String token = Encodes.encodeHex(Digests.sha1(strUserId.getBytes(), timeStr.getBytes()));
        SysUserToken userToken = new SysUserToken();
        userToken.setUserId(userId);
        userToken.setToken(token);
        userToken.setStatus(1);
        userToken.setCreateTime(nowDate);
        userToken.setCreateUser(user.getUsername());
        userToken.setLastUpdateTime(nowDate);
        userToken.setLastUpdateUser(user.getUsername());
        sysUserTokenMapper.insert(userToken);
        // 将Token放入缓存
        TokenCache.put(strUserId, token);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("userId", userId);
        result.put("token", token);
        return result;
    }

    @Override
    public void execAppLogout(Long userId) {
        if (userId == null) {
            throw new SystemException(ErrorCode.UNKNOWN, "用户没有登录");
        }

        sysUserTokenMapper.disableUserToken(userId, new Date());

        TokenCache.clean(String.valueOf(userId));
    }

    @Override
    public Object execAppAutoLogin(String path, String sign, Long userId) {
        if (userId == null || sign == null || "".equals(sign) || path == null || "".equals(path)) {
            return "FAIL";
        }

        SysUser user = sysUserMapper.selectByPrimaryKey(userId);
        if (user == null || user.getStatus() == 0) {
            return "FAIL";
        }

        String strUserId = String.valueOf(userId);
        String token = (String) TokenCache.get(strUserId);
        if (token == null) {
            Example example = new Example(SysUserToken.class);
            example.createCriteria().andEqualTo("userId", userId).andEqualTo("status", 1);
            List<SysUserToken> tokens = sysUserTokenMapper.selectByExample(example);
            if (tokens != null && !tokens.isEmpty()) {
                token = tokens.get(0).getToken();
                TokenCache.put(strUserId, token);
            }
        }

        if (token == null) {
            return "FAIL";
        } else {
            String serverSign = Encodes.encodeHex(Digests.sha1(token.getBytes(), path.getBytes()));
            if (sign.equals(serverSign)) {
                user.setLastLoginTime(new Date());
                sysUserMapper.updateByPrimaryKey(user);
                return "SUCCESS";
            } else {
                return "FAIL";
            }
        }
    }

    @Override
    public List<SysUser> getUsers(String username, String phoneNumber, String realName, String nickname) {
        Example example = new Example(SysUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", 1);
        if (username != null && !"".equals(username)) {
            criteria.andEqualTo("username", username);
        }
        if (phoneNumber != null && !"".equals(phoneNumber)) {
            criteria.andEqualTo("phoneNumber", phoneNumber);
        }
        if (realName != null && !"".equals(realName)) {
            criteria.andEqualTo("realName", realName);
        }
        if (nickname != null && !"".equals(nickname)) {
            criteria.andEqualTo("nickname", nickname);
        }
        List<SysUser> users = sysUserMapper.selectByExample(example);
        for (SysUser user : users) {
            user.setRoles(sysRoleMapper.getRolesByUserId(user.getId()));
        }
        return users;
    }

    @Override
    public void saveUsers(@RequestBody List<SysUser> users, SysUser operator) {
        String operatorName = operator.getUsername();
        Date operateDate = new Date();
        for (SysUser user : users) {
            //脏数据
            if (user.getId() == null && user.getStatus() != 1) {
                continue;
            }

            validateUser(user);

            if (user.getId() == null) {
                //新增
                //生成默认密码111111
                user.setSalt(user.getUsername());
                user.setPassword(PasswordHelper.generatePassword("gw123456", user.getSalt()));
                user.setCreateTime(operateDate);
                user.setCreateUser(operatorName);
                user.setLastUpdateTime(operateDate);
                user.setLastUpdateUser(operatorName);
                sysUserMapper.insertSelective(user);
                List<SysRole> roles = user.getRoles();
                if (roles != null) {
                    for (SysRole role : roles) {
                        SysUserRole sysUserRole = new SysUserRole();
                        sysUserRole.setUserId(user.getId());
                        sysUserRole.setRoleId(role.getId());
                        sysUserRole.setStatus(1);
                        sysUserRole.setCreateTime(operateDate);
                        sysUserRole.setCreateUser(operatorName);
                        sysUserRole.setLastUpdateTime(operateDate);
                        sysUserRole.setLastUpdateUser(operatorName);
                        sysUserRoleMapper.insertSelective(sysUserRole);
                    }
                }
            } else if (user.getStatus() == 0) {
                //删除
                user.setLastUpdateTime(operateDate);
                user.setLastUpdateUser(operatorName);
                sysUserMapper.updateByPrimaryKeySelective(user);
            } else {
                user.setLastUpdateTime(operateDate);
                user.setLastUpdateUser(operatorName);
                sysUserMapper.updateByPrimaryKeySelective(user);
                //修改
                List<SysRole> roles = user.getRoles();
                Example example = new Example(SysUserRole.class);
                example.createCriteria().andEqualTo("userId", user.getId()).andEqualTo("status", 1);
                List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectByExample(example);

                HashSet<Long> roleIds = new HashSet<>();
                if (roles != null) {
                    for (SysRole role : roles) {
                        roleIds.add(role.getId());
                    }
                }
                HashMap<Long, SysUserRole> sysUserRoleMap = new HashMap<>();
                for (SysUserRole userRole : sysUserRoles) {
                    sysUserRoleMap.put(userRole.getRoleId(), userRole);
                }
                HashSet<Long> duplicateIds = new HashSet<>();
                //去除前台接口ID集合与已经存在接口ID集合重复部分，前台剩余为新增，后台剩余为删除
                for (Long roleId : roleIds) {
                    if (sysUserRoleMap.keySet().contains(roleId)) {
                        duplicateIds.add(roleId);
                    }
                }

                for (Long roleId : duplicateIds) {
                    roleIds.remove(roleId);
                    sysUserRoleMap.remove(roleId);
                }

                for (Long roleId : roleIds) {
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setUserId(user.getId());
                    sysUserRole.setRoleId(roleId);
                    sysUserRole.setStatus(1);
                    sysUserRole.setCreateTime(operateDate);
                    sysUserRole.setCreateUser(operatorName);
                    sysUserRole.setLastUpdateTime(operateDate);
                    sysUserRole.setLastUpdateUser(operatorName);
                    sysUserRoleMapper.insertSelective(sysUserRole);
                }

                for (SysUserRole sysUserRole : sysUserRoleMap.values()) {
                    sysUserRole.setStatus(0);
                    sysUserRole.setLastUpdateTime(operateDate);
                    sysUserRole.setLastUpdateUser(operatorName);
                    sysUserRoleMapper.updateByPrimaryKeySelective(sysUserRole);
                }
            }
        }
    }

    /**
     * 验证用户
     *
     * @param user
     */
    private void validateUser(SysUser user) {
        //删除不做验证
        if (user.getStatus() == 0) {
            return;
        }
        if (user.getUsername() == null || "".equals(user.getUsername())) {
            throw new SystemException(ErrorCode.MODIFY_DATA_FAILED, "用户名不能为空！");
        }
        if (user.getPhoneNumber() == null || "".equals(user.getPhoneNumber())) {
            throw new SystemException(ErrorCode.MODIFY_DATA_FAILED, "手机号不能为空！");
        }
        Pattern pattern = Pattern.compile("^1[34578]\\d{9}$");
        Matcher matcher = pattern.matcher(user.getPhoneNumber());
        if (!matcher.find()) {
            throw new SystemException(ErrorCode.MODIFY_DATA_FAILED, "手机号格式不正确！");
        }
        if (user.getRealName() == null || "".equals(user.getRealName())) {
            throw new SystemException(ErrorCode.MODIFY_DATA_FAILED, "真实姓名不能为空！");
        }
        if (user.getNickname() == null || "".equals(user.getNickname())) {
            throw new SystemException(ErrorCode.MODIFY_DATA_FAILED, "昵称不能为空！");
        }

        //不能存在相同用户名的有效用户
        Example nameExample = new Example(SysUser.class);
        Example.Criteria nameCriteria = nameExample.createCriteria();
        nameCriteria.andEqualTo("status", 1);
        nameCriteria.andEqualTo("username", user.getUsername());
        if (user.getId() != null) {
            nameCriteria.andNotEqualTo("id", user.getId());
        }
        List<SysUser> users = sysUserMapper.selectByExample(nameExample);
        if (users.size() > 0) {
            throw new SystemException(ErrorCode.MODIFY_DATA_FAILED, "用户名已经存在不能使用！");
        }

        //一个手机号只能被一个有效用户使用
        Example phoneExample = new Example(SysUser.class);
        Example.Criteria phoneCriteria = phoneExample.createCriteria();
        phoneCriteria.andEqualTo("status", 1);
        phoneCriteria.andEqualTo("phoneNumber", user.getPhoneNumber());
        if (user.getId() != null) {
            phoneCriteria.andNotEqualTo("id", user.getId());
        }
        List<SysUser> users2 = sysUserMapper.selectByExample(phoneExample);
        if (users2.size() > 0) {
            throw new SystemException(ErrorCode.MODIFY_DATA_FAILED, "手机号已经被其他用户使用！");
        }
    }

    @Override
    public List<SysUser> getGrantAbleUsers() {
        return sysUserMapper.getGrantAbleUsers();
    }
}
