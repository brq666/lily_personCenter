package org.gwhere.controller;

import org.gwhere.model.SysCategoryItem;
import org.gwhere.service.CategoryItemService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/categoryItem")
public class CategoryItemController {

    @Resource
    private CategoryItemService categoryItemService;

    @RequestMapping("/getCategoryItems")
    public List<SysCategoryItem> getCategoryItems(String categoryValue) {
        return categoryItemService.getCategoryItems(categoryValue);
    }
}
