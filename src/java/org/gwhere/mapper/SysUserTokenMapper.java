package org.gwhere.mapper;

import org.apache.ibatis.annotations.Param;
import org.gwhere.model.SysUserToken;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

public interface SysUserTokenMapper extends Mapper<SysUserToken> {

    /**
     * 获取可用token
     * @param start
     * @param pageSize
     * @return
     */
    List<SysUserToken> getEnableTokens(@Param("start") int start, @Param("pageSize") int pageSize);

    /**
     * 注销用户token
     * @param userId
     * @param nowDate
     */
    void disableUserToken(@Param("userId") Long userId, @Param("nowDate") Date nowDate);
}
