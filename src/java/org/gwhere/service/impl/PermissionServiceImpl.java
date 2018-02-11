package org.gwhere.service.impl;

import org.gwhere.constant.Const;
import org.gwhere.mapper.SysUserMapper;
import org.gwhere.mapper.SysUserTokenMapper;
import org.gwhere.model.SysUser;
import org.gwhere.model.SysUserToken;
import org.gwhere.service.PermissionService;
import org.gwhere.utils.*;
import org.gwhere.vo.PermissionVO;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysUserTokenMapper sysUserTokenMapper;

    @Override
    public boolean authentication(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String url = requestUri.substring(contextPath.length());

        if (url.contains("/p/") || url.contains("/wx/") || url.contains("/customer/")) {
            return true;
        }

        //系统中不存在对应权限则验证失败
        PermissionVO permission = PermissionCache.get(url);
        if (permission == null) {
            return false;
        }

        Boolean isApp = Converter.to(Boolean.class).convert(request, "isApp");
        Boolean needLogin = permission.getNeedLogin();
        needLogin = needLogin == null ? false : needLogin;
        Boolean needPermission = permission.getNeedPermission();
        needPermission = needPermission == null ? false : needPermission;

        //前端访问
        if (isApp != null && isApp) {
            if (!needLogin) {
                return true;
            } else {
                Long userId = Converter.to(Long.class).convert(request, "userId");
                String sign = Converter.to(String.class).convert(request, "sign");
                if (userId == null || sign == null || "".equals(sign)) {
                    return false;
                }
                SysUser user = sysUserMapper.selectByPrimaryKey(userId);

                if (user == null || user.getStatus() == 0) {
                    return false;
                }
                String strUserId = String.valueOf(userId);
                String token = (String) TokenCache.get(strUserId);

                //缓存中没有则从数据库获取
                if (token == null) {
                    Example example = new Example(SysUserToken.class);
                    example.createCriteria().andEqualTo("userId", userId).andEqualTo("status", 1);
                    List<SysUserToken> tokens = sysUserTokenMapper.selectByExample(example);
                    if (tokens != null && !tokens.isEmpty()) {
                        SysUserToken userToken = tokens.get(0);
                        token = userToken.getToken();
                        //如果数据库中存在放入缓存
                        TokenCache.put(strUserId, token);
                    }
                }

                //获取token失败
                if (token == null) {
                    return false;
                }
                //获取服务器端签名
                String serverSign = Encodes.encodeHex(Digests.sha1(token.getBytes(), url.getBytes()));
                return sign.equals(serverSign);
            }
        } else {
            //需要用户登录或者需要验证权限
            if (needLogin || needPermission) {
                SysUser user = (SysUser) request.getSession().getAttribute(Const.SESSION_USER);
                //没有登录
                if (user == null) {
                    return false;
                }

                if (!needPermission) {
                    return true;
                }

                List<String> permissions = user.getPermissions();
                return permissions.contains(url);
            } else {
                return true;
            }
        }

    }
}
