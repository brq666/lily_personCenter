package org.gwhere.service;

import org.gwhere.model.SysUser;
import org.gwhere.vo.BrandInfoVO;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

public interface BrandService {

    Map<String, Object> getBrandConfigByCode(String code);

    Object getResourceSet(Long setId);

    //获取品牌信息
    List<Map<String, Object>> getBrandInfos(String name);

    //获取指定品牌所有信息
    BrandInfoVO getBrandInfo(String code);

    // 新增、修改或删除品牌信息
    void saveBrandInfos(List<BrandInfoVO> brandInfos, SysUser operator);
}
