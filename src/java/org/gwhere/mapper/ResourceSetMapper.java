package org.gwhere.mapper;

import org.gwhere.model.TblPcResPicture;
import org.gwhere.model.TblPcResourceSet;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ResourceSetMapper extends Mapper<TblPcResourceSet> {

    /**
     * 获取素材集
     *
     * @param id
     * @return
     */
    TblPcResourceSet getResourceSetById(Long id);
}