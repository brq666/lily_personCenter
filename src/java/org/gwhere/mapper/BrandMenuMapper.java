package org.gwhere.mapper;

import org.gwhere.model.TblPcBrandConfig;
import org.gwhere.model.TblPcBrandMenu;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMenuMapper extends Mapper<TblPcBrandMenu> {

    /**
     * 获取品牌配置
     *
     * @param code
     * @return
     */
    List<TblPcBrandMenu> getBrandMenuByCode(String code);
}