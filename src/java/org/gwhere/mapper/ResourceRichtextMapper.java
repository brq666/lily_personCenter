package org.gwhere.mapper;

import org.gwhere.model.TblPcResPicture;
import org.gwhere.model.TblPcResRichtext;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ResourceRichtextMapper extends Mapper<TblPcResRichtext> {

    /**
     * 获取图片
     *
     * @param resId
     * @return
     */
    List<TblPcResRichtext> getResourceRichtext(Long resId);
}