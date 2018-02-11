package org.gwhere.mapper;

import org.gwhere.model.TblPcResPicture;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ResourcePictureMapper extends Mapper<TblPcResPicture> {

    /**
     * 获取图片
     *
     * @param resId
     * @return
     */
    List<TblPcResPicture> getResourcePicture(Long resId);
}