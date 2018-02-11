package org.gwhere.mapper;

import org.gwhere.model.TblPcBrand;
import org.gwhere.model.TblPcBrandConfig;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandInfoMapper extends Mapper<TblPcBrand> {

    /**
     * 获取品牌配置
     *
     * @param code
     * @return
     */
    TblPcBrand getBrandInfoByCode(String code);

    /**
     * 获取品牌信息
     *
     * @param name
     * @return
     */
    List<TblPcBrand> getBrandsByName(String name);

    /**
     * 获取品牌信息
     *
     * @param name
     * @return
     */
    List<TblPcBrand> getBrandsByEqualName(String name);
}