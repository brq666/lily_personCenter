package org.gwhere.service.impl;

import org.apache.commons.io.FileUtils;
import org.gwhere.constant.Const;
import org.gwhere.mapper.FileInfoMapper;
import org.gwhere.model.SysUser;
import org.gwhere.model.TblPcFile;
import org.gwhere.service.FileService;
import org.gwhere.utils.PropertiesUtils;
import org.gwhere.utils.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileServiceImpl implements FileService {

    @Resource
    private FileInfoMapper fileInfoMapper;

    /**
     * 上传图片到图片库
     *
     * @param inputStream
     * @param operator
     * @throws Exception
     */
    public Map<String, Object> upLoadImage(TblPcFile file, InputStream inputStream, SysUser operator) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String operatorName = operator.getUsername();
        Date operateDate = new Date();
        String filePath = PropertiesUtils.getProperty("config/filePath.properties", "filePath");
        file.setCreateTime(operateDate);
        file.setCreateUser(operatorName);
        file.setLastUpdateTime(operateDate);
        file.setLastUpdateUser(operatorName);
        file.setStatus(1);
        // 上传图片库
        if ("brandPictures".equals(file.getGroupId())) {
            if (inputStream != null) {
                String uuid = StringUtils.generateUUID();
                File uploadFile = new File(filePath + file.getRelationId() + "/" + uuid + ".jpg");
//                result.put("filePath", Const.FILE_PATH + file.getRelationId() + "/" + uuid + ".jpg");
                result.put("filePath", "/" + file.getRelationId() + "/" + uuid + ".jpg");
                file.setFileName(uuid + ".jpg");
                file.setFilePath(filePath + file.getRelationId() + "/" + uuid + ".jpg");
                try {
                    FileUtils.copyInputStreamToFile(inputStream, uploadFile);
                    file.setFileSize(uploadFile.length());
                    fileInfoMapper.insertSelective(file);
                    return result;
                } catch (IOException e) {
                    throw new Exception("上传图片失败!\n" + e.getMessage());
                }
            }
        } else if ("brandLogo".equals(file.getGroupId())) {
            // 上传logo
            if (inputStream != null) {
                File uploadFile = new File(filePath + file.getRelationId() + "/" + "logo.jpg");
//                result.put("filePath", Const.FILE_PATH + file.getRelationId() + "/" + "logo.jpg");
                result.put("filePath", "/" + file.getRelationId() + "/" + "logo.jpg");
                file.setFileName("logo.jpg");
                file.setFilePath(filePath + file.getRelationId() + "/" + "logo.jpg");
                try {
                    FileUtils.copyInputStreamToFile(inputStream, uploadFile);
                    // 删除之前的logo
                    List<TblPcFile> logos = fileInfoMapper.getFilesByRelationId("brandLogo", file.getRelationId());
                    for (TblPcFile logo : logos) {
                        logo.setStatus(0);
                        fileInfoMapper.updateByPrimaryKeySelective(logo);
                    }
                    file.setFileSize(uploadFile.length());
                    fileInfoMapper.insertSelective(file);
                    return result;
                } catch (IOException e) {
                    throw new Exception("上传图片失败!\n" + e.getMessage());
                }
            }
        }
        return result;
    }

    /**
     * 根据groupId和relationId获取文件列表
     *
     * @param groupId
     * @param relationId
     * @return
     */
    public List<TblPcFile> getFilesByRelationId(String groupId, String relationId) {
        return fileInfoMapper.getFilesByRelationId(groupId, relationId);
    }
}
