package org.gwhere.service.impl;

import org.gwhere.constant.Const;
import org.gwhere.exception.ErrorCode;
import org.gwhere.exception.SystemException;
import org.gwhere.mapper.*;
import org.gwhere.model.*;
import org.gwhere.service.BrandService;
import org.gwhere.vo.BrandInfoVO;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.gwhere.utils.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class BrandServiceImpl implements BrandService {
    @Resource
    private BrandConfigMapper brandConfigMapper;
    @Resource
    private ResourcePictureMapper resourcePictureMapper;
    @Resource
    private ResourceSetMapper resourceSetMapper;
    @Resource
    private ResourceRichtextMapper resourceRichtextMapper;
    @Resource
    private BrandInfoMapper brandInfoMapper;
    @Resource
    private BrandMenuMapper brandMenuMapper;

    @Override
    public Map<String, Object> getBrandConfigByCode(String code) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> configMap = new HashMap<String, Object>();
        TblPcBrand brand = brandInfoMapper.getBrandInfoByCode(code);
        if (null != brand) {
            List<TblPcBrandMenu> menus = brandMenuMapper.getBrandMenuByCode(code);
            List<TblPcBrandConfig> list = brandConfigMapper.getBrandConfigByCode(code);
            if (!CollectionUtils.isEmpty(list)) {
                for (TblPcBrandConfig config : list) {
                    configMap.put(config.getItem(), config.getValue());
                }
            }
            result.put("id", brand.getId());
            result.put("name", brand.getName());
            result.put("config", configMap);
            result.put("menus", menus);
        }
        return result;
    }

    @Override
    public Object getResourceSet(Long setId) {
        TblPcResourceSet set = resourceSetMapper.getResourceSetById(setId);
        if (null != set) {
            if ("1".equals(set.getType())) {
                return resourceRichtextMapper.getResourceRichtext(set.getId());
            } else {
                return resourcePictureMapper.getResourcePicture(set.getId());
            }
        }
        return null;
    }

    //获取品牌信息
    public List<Map<String, Object>> getBrandInfos(String name) {
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        List<TblPcBrand> brands = brandInfoMapper.getBrandsByName(name);
        for (TblPcBrand brand : brands) {
            Map<String, Object> result = new HashMap<String, Object>();
            Map<String, Object> configMap = new HashMap<String, Object>();
            List<TblPcBrandConfig> list = brandConfigMapper.getBrandConfigByCode(brand.getCode());
            if (!CollectionUtils.isEmpty(list)) {
                for (TblPcBrandConfig config : list) {
                    if (config.getItem().equals("baseRestUrl") || config.getItem().equals("pointTypeId") || config.getItem().equals("cardTypeId")) {
                        configMap.put(config.getItem(), config.getValue());
                    }
                }
            }
            result.put("id", brand.getId());
            result.put("name", brand.getName());
            result.put("code", brand.getCode());
            result.put("status", brand.getStatus());
            result.put("config", configMap);
            results.add(result);
        }
        return results;
    }

    //获取指定品牌所有信息
    public BrandInfoVO getBrandInfo(String code) {
        BrandInfoVO result = new BrandInfoVO();
        //  获取tbl_pc_brand表数据
        TblPcBrand brand = brandInfoMapper.getBrandInfoByCode(code);
        result.setId(brand.getId());
        result.setName(brand.getName());
        result.setCode(brand.getCode());
        result.setStatus(brand.getStatus());
        // 获取tbl_pc_brand_menu表数据
        List<TblPcBrandMenu> menus = brandMenuMapper.getBrandMenuByCode(brand.getCode());
        result.setMenus(menus);
        // 获取tbl_pc_brand_config表数据
        List<TblPcBrandConfig> configs = brandConfigMapper.getBrandConfigByCode(brand.getCode());
        Map<String, Object> configsMap = new HashMap<>();
        for (TblPcBrandConfig config : configs) {
            configsMap.put(config.getItem(), config.getValue());
        }
        result.setConfig(configsMap);
        // 获取首页和轮播图
        List<TblPcResPicture> welcomeRS = resourcePictureMapper.getResourcePicture(Long.valueOf(configsMap.get("welcomeRsId").toString()));
        List<TblPcResPicture> bannerRS = resourcePictureMapper.getResourcePicture(Long.valueOf(configsMap.get("bannerRsId").toString()));
        result.setWelcomeRs(welcomeRS);
        result.setBannerRs(bannerRS);
        return result;
    }

    /**
     * 新增、修改或删除品牌信息
     *
     * @param brandInfos
     */
    public void saveBrandInfos(List<BrandInfoVO> brandInfos, SysUser operator) {
        String operatorName = operator.getUsername();
        Date operateDate = new Date();
        for (BrandInfoVO brandInfoVO : brandInfos) {
            // 脏数据
            if (brandInfoVO.getId() == null && brandInfoVO.getStatus() != 1) {
                continue;
            }

            validateBrandInfoVO(brandInfoVO);

            if (brandInfoVO.getId() == null) {
                // 新增数据
                // 生成品牌10位code
                String code = generateBrandCode();
                // 新增tbl_pc_brand表数据
                TblPcBrand brand = new TblPcBrand();
                brand.setName(brandInfoVO.getName());
                brand.setCode(code);
                brand.setStatus(brandInfoVO.getStatus());
                brand.setCreateTime(operateDate);
                brand.setCreateUser(operatorName);
                brand.setLastUpdateTime(operateDate);
                brand.setLastUpdateUser(operatorName);
                brandInfoMapper.insertSelective(brand);
                // 新增tbl_pc_resource_set表数据,用于保存welcomeRs和bannerRs
                TblPcResourceSet welcomeRs = new TblPcResourceSet();
                welcomeRs.setBrandId(brand.getId());
                welcomeRs.setType(Const.RESOURCE_IMAGE);
                welcomeRs.setStatus(1);
                welcomeRs.setCreateTime(operateDate);
                welcomeRs.setCreateUser(operatorName);
                welcomeRs.setLastUpdateTime(operateDate);
                welcomeRs.setLastUpdateUser(operatorName);
                resourceSetMapper.insertSelective(welcomeRs);
                TblPcResourceSet bannerRs = new TblPcResourceSet();
                bannerRs.setBrandId(brand.getId());
                bannerRs.setType(Const.RESOURCE_IMAGE);
                bannerRs.setStatus(1);
                bannerRs.setCreateTime(operateDate);
                bannerRs.setCreateUser(operatorName);
                bannerRs.setLastUpdateTime(operateDate);
                bannerRs.setLastUpdateUser(operatorName);
                resourceSetMapper.insertSelective(bannerRs);
                // 新增tbl_pc_brand_config表数据
                TblPcBrandConfig config = new TblPcBrandConfig();
                config.setBrandId(brand.getId());
                config.setStatus(1);
                config.setCreateTime(operateDate);
                config.setCreateUser(operatorName);
                config.setLastUpdateTime(operateDate);
                config.setLastUpdateUser(operatorName);
                Map<String, Object> configs = brandInfoVO.getConfig();
                if (configs != null) {
                    for (String item : configs.keySet()) {
                        TblPcBrandConfig temp = config.clone();
                        temp.setItem(item);
                        temp.setValue(configs.get(item).toString());
                        brandConfigMapper.insertSelective(temp);
                    }
                }
                // 新增tbl_pc_brand_config表数据用于保存welcomeRs和bannerRs
                TblPcBrandConfig welConfig = config.clone();
                welConfig.setItem("welcomeRsId");
                welConfig.setValue(welcomeRs.getId().toString());
                brandConfigMapper.insertSelective(welConfig);
                TblPcBrandConfig banConfig = config.clone();
                banConfig.setItem("bannerRsId");
                banConfig.setValue(bannerRs.getId().toString());
                brandConfigMapper.insertSelective(banConfig);
            } else {
                // 删除或修改
                // 删除只删除主表，不删除从表
                if (brandInfoVO.getStatus() == 1) {
                    // 修改tbl_pc_brand_config表数据
                    Map<String, Object> configs = brandInfoVO.getConfig();
                    if (configs != null) {
                        for (String item : configs.keySet()) {
                            TblPcBrandConfig config;
                            List<TblPcBrandConfig> list = brandConfigMapper.getBrandConfigByCodeAndItem(brandInfoVO.getCode(),item);
                            // list为null，表示新增
                            if(list == null || list.isEmpty()) {
                                config = new TblPcBrandConfig();
                                config.setBrandId(brandInfoVO.getId());
                                config.setItem(item);
                                config.setValue(configs.get(item).toString());
                                config.setStatus(brandInfoVO.getStatus());
                                config.setCreateTime(operateDate);
                                config.setCreateUser(operatorName);
                                config.setLastUpdateTime(operateDate);
                                config.setLastUpdateUser(operatorName);
                                brandConfigMapper.insertSelective(config);
                            }else {
                                // 修改
                                config = list.get(0);
                                config.setValue(configs.get(item).toString());
                                config.setLastUpdateTime(operateDate);
                                config.setLastUpdateUser(operatorName);
                                brandConfigMapper.updateByPrimaryKeySelective(config);
                            }
                        }
                    }

                    // 修改tbl_pc_brand_menu表数据
                    List<TblPcBrandMenu> menus = brandInfoVO.getMenus();
                    if (menus != null) {
                        for (TblPcBrandMenu menu : menus) {
                            // 脏数据
                            if (menu.getId() == null && menu.getStatus() != 1) {
                                continue;
                            }
                            // 新增菜单
                            if (menu.getId() == null) {
                                menu.setCreateTime(operateDate);
                                menu.setCreateUser(operatorName);
                                menu.setLastUpdateTime(operateDate);
                                menu.setLastUpdateUser(operatorName);
                                brandMenuMapper.insertSelective(menu);
                            } else {
                                // 删除或者修改
                                menu.setLastUpdateTime(operateDate);
                                menu.setLastUpdateUser(operatorName);
                                brandMenuMapper.updateByPrimaryKeySelective(menu);
                            }
                        }
                    }

                    // 修改tbl_pc_resource_picture表数据
                    List<TblPcResPicture> welcomeRS = brandInfoVO.getWelcomeRs();
                    if (welcomeRS != null) {
                        for (TblPcResPicture welcome : welcomeRS) {
                            // 脏数据
                            if (welcome.getId() == null && welcome.getStatus() != 1) {
                                continue;
                            }
                            // 新增菜单
                            if (welcome.getId() == null) {
                                welcome.setCreateTime(operateDate);
                                welcome.setCreateUser(operatorName);
                                welcome.setLastUpdateTime(operateDate);
                                welcome.setLastUpdateUser(operatorName);
                                resourcePictureMapper.insertSelective(welcome);
                            } else {
                                // 删除或者修改
                                welcome.setLastUpdateTime(operateDate);
                                welcome.setLastUpdateUser(operatorName);
                                resourcePictureMapper.updateByPrimaryKeySelective(welcome);
                            }
                        }
                    }
                    List<TblPcResPicture> bannerRS = brandInfoVO.getBannerRs();
                    if (bannerRS != null) {
                        for (TblPcResPicture banner : bannerRS) {
                            // 脏数据
                            if (banner.getId() == null && banner.getStatus() != 1) {
                                continue;
                            }
                            // 新增菜单
                            if (banner.getId() == null) {
                                banner.setCreateTime(operateDate);
                                banner.setCreateUser(operatorName);
                                banner.setLastUpdateTime(operateDate);
                                banner.setLastUpdateUser(operatorName);
                                resourcePictureMapper.insertSelective(banner);
                            } else {
                                // 删除或者修改
                                banner.setLastUpdateTime(operateDate);
                                banner.setLastUpdateUser(operatorName);
                                resourcePictureMapper.updateByPrimaryKeySelective(banner);
                            }
                        }
                    }
                }
                // 删除或修改tbl_pc_brand表数据
                TblPcBrand brand = new TblPcBrand();
                brand.setId(brandInfoVO.getId());
                brand.setName(brandInfoVO.getName());
                brand.setStatus(brandInfoVO.getStatus());
                brand.setLastUpdateTime(operateDate);
                brand.setLastUpdateUser(operatorName);
                brandInfoMapper.updateByPrimaryKeySelective(brand);
            }
        }
    }

    /**
     * 生成10位品牌code
     *
     * @return
     */
    private String generateBrandCode() {
        String code;
        TblPcBrand brand;
        do {
            code = StringUtils.GetRandomString(10);
            brand = brandInfoMapper.getBrandInfoByCode(code);
        } while (brand != null);
        return code;
    }

    /**
     * 验证品牌信息是否合法
     *
     * @param brandInfoVO
     */
    private void validateBrandInfoVO(BrandInfoVO brandInfoVO) {

        // 删除不做验证
        if (brandInfoVO.getStatus() == 0) {
            return;
        }
        if (brandInfoVO.getName() == null || "".equals(brandInfoVO.getName())) {
            throw new SystemException(ErrorCode.MODIFY_DATA_FAILED, "品牌名称不能为空！");
        }
        for (String item : brandInfoVO.getConfig().keySet()) {
            String value = brandInfoVO.getConfig().get(item).toString();
            if (value == null || "".equals(value)) {
                if ("baseRestUrl".equals(item)) {
                    throw new SystemException(ErrorCode.MODIFY_DATA_FAILED, "品牌业务数据获取地址不能为空！");
                } else if ("cardTypeId".equals(item)) {
                    throw new SystemException(ErrorCode.MODIFY_DATA_FAILED, "卡类型不能为空！");
                } else if ("pointTypeId".equals(item)) {
                    throw new SystemException(ErrorCode.MODIFY_DATA_FAILED, "积分类型不能为空！");
                }
            }
        }
        // 新增品牌名字验重
        if (brandInfoVO.getId() == null) {
            List<TblPcBrand> brands = brandInfoMapper.getBrandsByEqualName(brandInfoVO.getName().toLowerCase());
            if (brands != null && !brands.isEmpty()) {
                throw new SystemException(ErrorCode.MODIFY_DATA_FAILED, "品牌名称不能重复！");
            }
        } else {
            // 修改品牌，验证与其他品牌名字是否重复
            List<TblPcBrand> brands = brandInfoMapper.getBrandsByEqualName(brandInfoVO.getName().toLowerCase());
            for (TblPcBrand brand : brands) {
                if (!brandInfoVO.getId().equals(brand.getId())) {
                    throw new SystemException(ErrorCode.MODIFY_DATA_FAILED, "品牌名称不能重复！");
                }
            }
        }
    }
}