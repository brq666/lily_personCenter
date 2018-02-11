package org.gwhere.service.impl;

import org.gwhere.exception.ErrorCode;
import org.gwhere.exception.SystemException;
import org.gwhere.mapper.SysCategoryItemMapper;
import org.gwhere.mapper.SysCategoryMapper;
import org.gwhere.model.SysCategory;
import org.gwhere.model.SysCategoryItem;
import org.gwhere.model.SysUser;
import org.gwhere.service.CategoryService;
import org.gwhere.utils.ValidateUtil;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private SysCategoryMapper categoryMapper;

    @Resource
    private SysCategoryItemMapper categoryItemMapper;

    @Override
    public List<SysCategory> getCategories(String categoryName, String categoryValue) {
        Example example = new Example(SysCategory.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", 1);
        if (categoryName != null && !"".equals(categoryName)) {
            criteria.andEqualTo("categoryName", categoryName);
        }
        if (categoryValue != null && !"".equals(categoryValue)) {
            criteria.andEqualTo("categoryValue", categoryValue);
        }
        List<SysCategory> categories = categoryMapper.selectByExample(example);
        for (SysCategory category : categories) {
            Example itemExample = new Example(SysCategoryItem.class);
            itemExample.createCriteria().andEqualTo("status", 1)
                    .andEqualTo("categoryId", category.getId());
            itemExample.orderBy("itemOrder");
            List<SysCategoryItem> items = categoryItemMapper.selectByExample(itemExample);
            category.setItems(items);
        }
        return categories;
    }

    @Override
    public void saveCategories(List<SysCategory> categories, SysUser operator) {
        validateCategories(categories);
        String operatorName = operator.getUsername();
        Date operateDate = new Date();
        for (SysCategory category : categories) {
            //脏数据
            if (category.getId() == null && category.getStatus() != 1) {
                continue;
            }
            if (category.getId() == null) {
                //新增数据
                category.setCreateTime(operateDate);
                category.setCreateUser(operatorName);
                category.setLastUpdateTime(operateDate);
                category.setLastUpdateUser(operatorName);
                categoryMapper.insertSelective(category);
                List<SysCategoryItem> items = category.getItems();
                if (items != null && !items.isEmpty()) {
                    saveCategoryItem(category, items, operateDate, operatorName);
                }
            } else {
                //修改和删除
                category.setLastUpdateTime(operateDate);
                category.setLastUpdateUser(operatorName);
                categoryMapper.updateByPrimaryKeySelective(category);
                //修改
                if (category.getStatus() == 1) {
                    List<SysCategoryItem> items = category.getItems();
                    if (items != null && !items.isEmpty()) {
                        saveCategoryItem(category, items, operateDate, operatorName);
                    }
                }
            }
        }
    }

    /**
     * 保存小类
     */
    private void saveCategoryItem(SysCategory category, List<SysCategoryItem> items,
                                  Date operateDate, String operatorName) {
        int itemOrder = 1;
        for (SysCategoryItem item : items) {
            //脏数据
            if (item.getId() == null && item.getStatus() != 1) {
                continue;
            }
            item.setCategoryId(category.getId());
            //可用小类
            if (item.getStatus() == 1) {
                item.setItemOrder(itemOrder++);
            }
            item.setLastUpdateTime(operateDate);
            item.setLastUpdateUser(operatorName);
            if (item.getId() == null) {
                //新增
                item.setCreateTime(operateDate);
                item.setCreateUser(operatorName);
                categoryItemMapper.insertSelective(item);
            } else {
                //修改和删除
                categoryItemMapper.updateByPrimaryKeySelective(item);
            }

        }
    }

    /**
     * 验证大类
     */
    private void validateCategories(List<SysCategory> categories) {
        //获取所有现存大类
        Example example = new Example(SysCategory.class);
        example.createCriteria().andEqualTo("status", 1);
        List<SysCategory> existCategories = categoryMapper.selectByExample(example);
        Map<String, Long> nameMap = new HashMap<>();
        Map<String, Long> valueMap = new HashMap<>();
        for (SysCategory category : existCategories) {
            nameMap.put(category.getCategoryName(), category.getId());
            valueMap.put(category.getCategoryValue(), category.getId());
        }
        for (SysCategory category : categories) {
            //删除或者脏数据无需验证
            if (category.getStatus() != 1) {
                continue;
            }
            ValidateUtil.validate(category);

            if (nameMap.containsKey(category.getCategoryName())
                    && (nameMap.get(category.getCategoryName()) == null
                    || !nameMap.get(category.getCategoryName()).equals(category.getId()))) {
                throw new SystemException(ErrorCode.UNKNOWN, "大类名称不能重复！");
            }

            if (valueMap.containsKey(category.getCategoryValue())
                    && (valueMap.get(category.getCategoryValue()) == null
                    || !valueMap.get(category.getCategoryValue()).equals(category.getId()))) {
                throw new SystemException(ErrorCode.UNKNOWN, "大类代码不能重复！");
            }

            nameMap.put(category.getCategoryName(), category.getId());
            valueMap.put(category.getCategoryValue(), category.getId());

            List<SysCategoryItem> items = category.getItems();
            if (items != null && !items.isEmpty()) {
                validateItems(items);
            }

        }
    }


    /**
     * 验证小类
     */
    private void validateItems(List<SysCategoryItem> items) {
        Set<String> itemNames = new HashSet<>();
        Set<String> itemValues = new HashSet<>();
        for (SysCategoryItem item : items) {
            //删除或者脏数据无需验证
            if (item.getStatus() != 1) {
                continue;
            }
            ValidateUtil.validate(item);
            if (itemNames.contains(item.getItemName())) {
                throw new SystemException(ErrorCode.UNKNOWN, "小类名称不能重复！");
            } else {
                itemNames.add(item.getItemName());
            }
            if (itemValues.contains(item.getItemValue())) {
                throw new SystemException(ErrorCode.UNKNOWN, "小类代码不能重复！");
            } else {
                itemValues.add(item.getItemValue());
            }
        }

    }
}
