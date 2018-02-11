package org.gwhere.mapper;

import org.gwhere.model.SysCategoryItem;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SysCategoryItemMapper extends Mapper<SysCategoryItem> {

    /**
     * 获取小类值
     *
     * @param categoryValue
     * @return
     */
    List<SysCategoryItem> getCategoryItems(String categoryValue);
}