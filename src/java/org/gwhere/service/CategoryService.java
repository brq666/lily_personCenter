package org.gwhere.service;

import org.gwhere.model.SysCategory;
import org.gwhere.model.SysUser;

import java.util.List;

public interface CategoryService {

    /**
     * 获取大类
     *
     * @param categoryName
     * @param categoryValue
     * @return
     */
    List<SysCategory> getCategories(String categoryName, String categoryValue);

    /**
     * 保存大类
     *
     * @param operator
     */
    void saveCategories(List<SysCategory> categories, SysUser operator);
}
