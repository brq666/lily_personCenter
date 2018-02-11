package org.gwhere.service.impl;

import org.gwhere.mapper.SysCategoryItemMapper;
import org.gwhere.model.SysCategoryItem;
import org.gwhere.service.CategoryItemService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CategoryItemServiceImpl implements CategoryItemService {

    @Resource
    private SysCategoryItemMapper categoryItemMapper;

    @Override
    public List<SysCategoryItem> getCategoryItems(String categoryValue) {
        return categoryItemMapper.getCategoryItems(categoryValue);
    }
}
