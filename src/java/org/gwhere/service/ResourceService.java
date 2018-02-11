package org.gwhere.service;

import org.gwhere.model.SysResource;
import org.gwhere.model.SysUser;

import java.util.List;

public interface ResourceService {

    /**
     * 获取菜单资源
     *
     * @param userId
     * @return
     */
    List<SysResource> getMenuResources(Long userId);

    /**
     * 获取系统资源
     *
     * @return
     */
    List<SysResource> getAllBackResources();

    /**
     * 获取资源树
     *
     * @return
     */
    List<SysResource> getResourcesForTree();

    /**
     * 保存资源
     *
     * @param resource
     */
    SysResource saveResource(SysResource resource, SysUser operator);
}
