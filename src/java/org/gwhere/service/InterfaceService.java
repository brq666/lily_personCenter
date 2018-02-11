package org.gwhere.service;

import org.gwhere.model.SysInterface;
import org.gwhere.model.SysUser;

import java.util.List;

public interface InterfaceService {

    /**
     * 获取接口
     *
     * @param name 接口名称
     * @param path 接口路径
     * @return
     */
    List<SysInterface> getInterfaces(String name, String path, String existIds);

    /**
     * 保存接口
     *
     * @param interfaces
     */
    void saveInterfaces(List<SysInterface> interfaces, SysUser operator);
}
