package org.gwhere.controller;

import com.alibaba.fastjson.JSONObject;
import org.gwhere.constant.Const;
import org.gwhere.model.SysUser;
import org.gwhere.model.TblPcFile;
import org.gwhere.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FileService fileService;

    @RequestMapping("/uploadFile")
    public Map<String, Object> uploadFile(@RequestParam Map<String, Object> params, @RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile, HttpSession session) throws Exception {
        SysUser operator = (SysUser) session.getAttribute(Const.SESSION_USER);
        InputStream inputStream = uploadFile == null ? null : uploadFile.getInputStream();
        TblPcFile pcFile = new TblPcFile();
        pcFile.setGroupId(params.get("groupId").toString());
        pcFile.setRelationId(params.get("relationId").toString());
        pcFile.setFileType(params.get("fileType").toString());
        return fileService.upLoadImage(pcFile, inputStream, operator);
    }

    //获取品牌信息
    @RequestMapping("/getFiles")
    @ResponseBody
    public List<TblPcFile> getFiles(@RequestBody JSONObject data) {
        String groupId = data.getString("groupId");
        String relationId = data.getString("relationId");
        return fileService.getFilesByRelationId(groupId, relationId);
    }
}
