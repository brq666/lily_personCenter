package org.gwhere.mapper;

import org.apache.ibatis.annotations.Param;
import org.gwhere.model.TblPcFile;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface FileInfoMapper extends Mapper<TblPcFile> {
    /**
     * 根据groupId和relationId获取文件列表
     *
     * @param groupId
     * @param relationId
     * @return
     */
    List<TblPcFile> getFilesByRelationId(@Param("groupId") String groupId, @Param("relationId") String relationId);
}
