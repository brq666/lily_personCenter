package org.gwhere.controller;

import org.gwhere.constant.Const;
import org.gwhere.database.PageHelper;
import org.gwhere.model.SysCategory;
import org.gwhere.model.SysUser;
import org.gwhere.service.CategoryService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @RequestMapping("/getCategories")
    public List<SysCategory> getCategories(String categoryName, String categoryValue) {
        PageHelper.startPage();
        return categoryService.getCategories(categoryName, categoryValue);
    }

    @RequestMapping("/saveCategories")
    public void saveCategories(@RequestBody List<SysCategory> categories, HttpSession session) {
        SysUser operator = (SysUser) session.getAttribute(Const.SESSION_USER);
        categoryService.saveCategories(categories, operator);
    }
}
