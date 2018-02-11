package org.gwhere.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.gwhere.constant.Const;
import org.gwhere.database.PageHelper;
import org.gwhere.model.SysRequestLog;
import org.gwhere.model.SysUser;
import org.gwhere.model.SysUserAction;
import org.gwhere.model.TblPcBrandConfig;
import org.gwhere.service.BrandService;
import org.gwhere.utils.PropertiesUtils;
import org.gwhere.utils.StringUtils;
import org.gwhere.vo.BrandInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    //获取配置信息
    @RequestMapping("/getBrandConfig")
    @ResponseBody
    public Map<String, Object> getBrandConfig(@RequestParam("code") String code) throws Exception {
        return brandService.getBrandConfigByCode(code);
    }

    //获取素材集
    @RequestMapping("/getResourceSet")
    @ResponseBody
    public Object getResourceSet(@RequestParam("setId") Long setId) throws Exception {
        return brandService.getResourceSet(setId);
    }

    //获取品牌信息
    @RequestMapping("/getBrandInfos")
    public List<Map<String, Object>> getBrandInfos(String name) {
        PageHelper.startPage();
        return brandService.getBrandInfos(name);
    }

    //获取指定品牌所有信息
    @RequestMapping("/getBrandInfo")
    @ResponseBody
    public BrandInfoVO getBrandInfo(@RequestParam("code") String code) throws Exception {
        return brandService.getBrandInfo(code);
    }

    /**
     * 新增、修改或删除品牌信息
     *
     * @param brandInfos
     */
    @RequestMapping("/saveBrandInfos")
    public void saveBrandInfos(@RequestBody List<BrandInfoVO> brandInfos, HttpSession session) {
        SysUser operator = (SysUser) session.getAttribute(Const.SESSION_USER);
        brandService.saveBrandInfos(brandInfos, operator);
    }
}
