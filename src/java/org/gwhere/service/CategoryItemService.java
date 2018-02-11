package org.gwhere.service;

import org.gwhere.model.SysCategoryItem;

import java.util.List;

public interface CategoryItemService {

    /**
     * 获取小类
     *
     * @param categoryValue
     * @return
     */
    List<SysCategoryItem> getCategoryItems(String categoryValue);

}
