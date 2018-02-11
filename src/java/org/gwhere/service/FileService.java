package org.gwhere.service;

import org.gwhere.model.SysUser;
import org.gwhere.model.TblPcFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface FileService {

    /**
     * 上传图片到图片库
     *
     * @param inputStream
     * @param operator
     * @throws Exception
     */
    Map<String, Object> upLoadImage(TblPcFile file, InputStream inputStream, SysUser operator) throws Exception;

    /**
     * 根据groupId和relationId获取文件列表
     *
     * @param groupId
     * @param relationId
     * @return
     */
    List<TblPcFile> getFilesByRelationId(String groupId, String relationId);
}
