package org.gwhere.mapper;

import org.apache.ibatis.annotations.Param;
import org.gwhere.model.TblPcBrandConfig;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandConfigMapper extends Mapper<TblPcBrandConfig> {

    /**
     * 获取品牌配置
     *
     * @param code
     * @return
     */
    List<TblPcBrandConfig> getBrandConfigByCode(String code);

    /**
     * 获取品牌指定配置项
     */
    List<TblPcBrandConfig> getBrandConfigByCodeAndItem(@Param("code") String code, @Param("item") String item);
}