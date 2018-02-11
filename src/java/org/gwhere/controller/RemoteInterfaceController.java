package org.gwhere.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.gwhere.constant.Const;
import org.gwhere.model.SysRequestLog;
import org.gwhere.model.SysUser;
import org.gwhere.service.RemoteInterfaceService;
import org.gwhere.utils.PropertiesUtils;
import org.gwhere.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/remoteInterface")
public class RemoteInterfaceController {

    @Autowired
    private RemoteInterfaceService remoteInterfaceService;

    //统一接口
    @RequestMapping(value = "/invoke")
    @ResponseBody
    public Object invoke(@RequestBody JSONObject data, HttpSession session) {
//        String url = data.getString("url");
//        String str = null;
//        if(url.contains("pointInfo")){
//            str = "[\n" +
//                    "{\n" +
//                    "\"card_no\":\"WX20160315358682\",\n" +
//                    "\"card_type\":\"lily卡\",\n" +
//                    "\"card_grade\":\"2\",\n" +
//                    "\"grade_expire\":\"2117-11-01\",\n" +
//                    "\"point_type\":\"1\",\n" +
//                    "\"grade_up_point\":\"2880\",\n" +
//                    "\"point\":24831\n" +
//                    "}\n" +
//                    "]\n";
//        }else if(url.contains("card")){
//            str = "[\n" +
//                    "{\n" +
//                    "\"pointType\":[\n" +
//                    "{\n" +
//                    "\"pointType\":1,\n" +
//                    "\"pointName\":\"积分\"\n" +
//                    "},\n" +
//                    "{\n" +
//                    "\"pointType\":2,\n" +
//                    "\"pointName\":\"经验\"\n" +
//                    "}\n" +
//                    "],\"changeType\":[\n" +
//                    "{\n" +
//                    "\"changeType\":1,\n" +
//                    "\"changeName\":\"生日积分\"\n" +
//                    "},\n" +
//                    "{\n" +
//                    "\"changeType\":2,\n" +
//                    "\"changeName\":\"手动变更\"\n" +
//                    "}\n" +
//                    "],\n" +
//                    "\n" +
//                    "\"grade\":[\n" +
//                    "                {\n" +
//                    "                    \"gradeType\":1,\n" +
//                    "                    \"gradeName\":\"粉丝\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"gradeType\":2,\n" +
//                    "                    \"gradeName\":\"普卡会员\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"gradeType\":3,\n" +
//                    "                    \"gradeName\":\"银卡会员\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"gradeType\":4,\n" +
//                    "                    \"gradeName\":\"金卡会员\"\n" +
//                    "                }\n" +
//                    "            ]\n" +
//                    "        }\n" +
//                    "    ]\n";
//        }else if(url.contains("pointlog")){
//            str = "[\n" +
//                    "        {\n" +
//                    "            \"card_id\":\"WX20160315358682\",\n" +
//                    "            \"card_type_id\":\"1\",\n" +
//                    "            \"change_value\":1,\n" +
//                    "            \"old_value\":\"4832\",\n" +
//                    "            \"current_value\":4831,\n" +
//                    "            \"source\":\"2\",\n" +
//                    "            \"order_id\":\"\",\n" +
//                    "            \"remark\":\"\",\n" +
//                    "            \"change_time\":\"2017-11-30 17:29:26\",\n" +
//                    "            \"point_type_id\":1\n" +
//                    "        },\n" +
//                    "        {\n" +
//                    "            \"card_id\":\"WX20160315358682\",\n" +
//                    "            \"card_type_id\":\"1\",\n" +
//                    "            \"change_value\":1,\n" +
//                    "            \"old_value\":\"4833\",\n" +
//                    "            \"current_value\":4832,\n" +
//                    "            \"source\":\"2\",\n" +
//                    "            \"order_id\":\"\",\n" +
//                    "            \"remark\":\"\",\n" +
//                    "            \"change_time\":\"2017-11-30 17:29:13\",\n" +
//                    "            \"point_type_id\":1\n" +
//                    "        }\n" +
//                    "    ]\n";
//        }else if(url.contains("queryUserByOpenId")){
//            str = "[\n" +
//                    "        {\n" +
//                    "\"mobile\":\"18818109708\",\n" +
//                    "\"name\": \"douglas\",\n" +
//                    "\"sex\": \"男\",\n" +
//                    "\"birthday\":\"1990-11-11\",\n" +
//                    "\"job\": \"程序员\",\n" +
//                    "\"mail\": \"douglas@163.com\",\n" +
//                    "\"province\":\"310000\",\n" +
//                    "\"city\":\"310100\",\n" +
//                    "\"district\":\"310115\",\n" +
//                    "\"address\": \"天钥桥路1000号\",\n" +
//                    "\"openId\":\"dasdasdasfsdsacas\",\n" +
//                    "\"zipCode\":\"200000\"\n" +
//                    "        }\n" +
//                    "]\n";
//        }else if(url.contains("queryUserByMobile")){
//            str = "[\n" +
//                    "        {\n" +
//                    "            \"mobile\":\"18818109708\",\n" +
//                    "            \"name\": \"douglas\",\n" +
//                    "            \"sex\": \"男\",\n" +
//                    "            \"birthday\":\"1990-11-11\",\n" +
//                    "            \"job\": \"程序员\",\n" +
//                    "\"mail\": \"douglas@163.com\",\n" +
//                    "\"province\":\"310000\",\n" +
//                    "\"city\":\"310100\",\n" +
//                    "\"district\":\"310115\",\n" +
//                    "\"address\": \"徐汇区天钥桥路1000号\",\n" +
//                    "\"openId\":\"dasdasdasfsdsacas\",\n" +
//                    "\"zipCode\":\"200000\"\n" +
//                    "        }\n" +
//                    "    ]\n";
//        }else if(url.contains("register")){
//            str = "{\n" +
//                    "        \"status\": \"注册成功\"\n" +
//                    "    }\n";
//        }else if(url.contains("modifyWxUser")){
//            str = " {\n" +
//                    "        \"status\": \"用户数据更新成功\"\n" +
//                    "    }\n";
//        }else if(url.contains("getlist")){
//            str = "[{\n" +
//                    "    \"coupon_type_id\":\"52\",\n" +
//                    "    \"coupon_id\":\"52611315\",\n" +
//                    "    \"customerno\":\"owLyUjpIE0cfIJcGOZlMvZo28Ykk\",\n" +
//                    "    \"plat_code\":\"weixin\",\n" +
//                    "    \"start_time\":\"2017-11-21 22:54:01.0\",\n" +
//                    "    \"end_time\":\"2018-12-15 00:00:00.0\",\n" +
//                    "    \"payment\":150,\n" +
//                    "    \"coupontype\":\"会员节新开卡赠券\",\n" +
//                    "    \"couponrule\":\"5.5折以上商品可以使用，1件商品限用1张电子券，可在北京、武汉、上海、深圳、广州、东莞、成都、哈尔滨、西安、长春、天津、厦门、大连、乌鲁木齐、合肥、芜湖、台州、宁波、金华、甘肃、延吉、青岛的授权店铺和Lily官方商城使用。\",\n" +
//                    "    \"couponcolor\":\"\",\n" +
//                    "    \"couponstatus\":\"SENT\"\n" +
//                    "},\n" +
//                    "{\n" +
//                    "    \"coupon_type_id\":\"52\",\n" +
//                    "    \"coupon_id\":\"52611315\",\n" +
//                    "    \"customerno\":\"owLyUjpIE0cfIJcGOZlMvZo28Ykk\",\n" +
//                    "    \"plat_code\":\"weixin\",\n" +
//                    "    \"start_time\":\"2017-11-21 22:54:01.0\",\n" +
//                    "    \"end_time\":\"2018-12-15 00:00:00.0\",\n" +
//                    "    \"payment\":250,\n" +
//                    "    \"coupontype\":\"会员节新开卡赠券\",\n" +
//                    "    \"couponrule\":\"5.5折以上商品可以使用，1件商品限用1张电子券，可在北京、武汉、上海、深圳、广州、东莞、成都、哈尔滨、西安、长春、天津、厦门、大连、乌鲁木齐、合肥、芜湖、台州、宁波、金华、甘肃、延吉、青岛的授权店铺和Lily官方商城使用。\",\n" +
//                    "    \"couponcolor\":\"\",\n" +
//                    "    \"couponstatus\":\"USED\"\n" +
//                    "},\n" +
//                    "{\n" +
//                    "    \"coupon_type_id\":\"52\",\n" +
//                    "    \"coupon_id\":\"52611315\",\n" +
//                    "    \"customerno\":\"owLyUjpIE0cfIJcGOZlMvZo28Ykk\",\n" +
//                    "    \"plat_code\":\"weixin\",\n" +
//                    "    \"start_time\":\"2017-11-21 22:54:01.0\",\n" +
//                    "    \"end_time\":\"2017-12-15 00:00:00.0\",\n" +
//                    "    \"payment\":150,\n" +
//                    "    \"coupontype\":\"会员节新开卡赠券\",\n" +
//                    "    \"couponrule\":\"5.5折以上商品可以使用，1件商品限用1张电子券，可在北京、武汉、上海、深圳、广州、东莞、成都、哈尔滨、西安、长春、天津、厦门、大连、乌鲁木齐、合肥、芜湖、台州、宁波、金华、甘肃、延吉、青岛的授权店铺和Lily官方商城使用。\",\n" +
//                    "    \"couponcolor\":\"\",\n" +
//                    "    \"couponstatus\":\"SENT\"\n" +
//                    "},\n" +
//                    "{\n" +
//                    "    \"coupon_type_id\":\"52\",\n" +
//                    "    \"coupon_id\":\"52611315\",\n" +
//                    "    \"customerno\":\"owLyUjpIE0cfIJcGOZlMvZo28Ykk\",\n" +
//                    "    \"plat_code\":\"weixin\",\n" +
//                    "    \"start_time\":\"2017-11-21 22:54:01.0\",\n" +
//                    "    \"end_time\":\"2018-12-15 00:00:00.0\",\n" +
//                    "    \"payment\":50,\n" +
//                    "    \"coupontype\":\"会员节新开卡赠券\",\n" +
//                    "    \"couponrule\":\"5.5折以上商品可以使用，1件商品限用1张电子券，可在北京、武汉、上海、深圳、广州、东莞、成都、哈尔滨、西安、长春、天津、厦门、大连、乌鲁木齐、合肥、芜湖、台州、宁波、金华、甘肃、延吉、青岛的授权店铺和Lily官方商城使用。\",\n" +
//                    "    \"couponcolor\":\"\",\n" +
//                    "    \"couponstatus\":\"SENT\"\n" +
//                    "}\n" +
//                    "]\n";
//        }else if(url.contains("queryOrderByUser")){
//            str = "[{\n" +
//                    "\"order_id\":\"12342536342\",\n" +
//                    "\"shop_name\":\"店铺名\",\n" +
//                    "\"plat_name\":\"京东\",\n" +
//                    "\"payment\":200.21,\n" +
//                    "\"created\":\"2017/12/27\",\n" +
//                    "\"status\":\"交易成功\",\n" +
//                    "\"modified\":\"2017/12/27\",\n" +
//                    "\"is_refund\":\"\",\n" +
//                    "\"itemlist\":[{\n" +
//                    "      \"product_name\":\"商品名\",\n" +
//                    "      \"product_num\":12,\n" +
//                    "      \"total_fee\":200.12,\n" +
//                    "      \"itempayment\":200.12,\n" +
//                    "      \"discount_rate\":1.00\n" +
//                    "  },{" +
//                    "      \"product_name\":\"商品名\",\n" +
//                    "      \"product_num\":12,\n" +
//                    "      \"total_fee\":200.12,\n" +
//                    "      \"itempayment\":200.12,\n" +
//                    "      \"discount_rate\":1.00\n" +
//                    "  },{" +
//                    "      \"product_name\":\"商品名\",\n" +
//                    "      \"product_num\":12,\n" +
//                    "      \"total_fee\":200.12,\n" +
//                    "      \"itempayment\":200.12,\n" +
//                    "      \"discount_rate\":1.00\n" +
//                    "}]\n" +
//                    "},\n" +
//                    "{\n" +
//                    "\"order_id\":\"12342536342\",\n" +
//                    "\"shop_name\":\"店铺名\",\n" +
//                    "\"plat_name\":\"京东\",\n" +
//                    "\"payment\":200.21,\n" +
//                    "\"created\":\"2017/12/27\",\n" +
//                    "\"status\":\"交易成功\",\n" +
//                    "\"modified\":\"2017/12/27\",\n" +
//                    "\"is_refund\": true,\n" +
//                    "\"itemlist\":[{\n" +
//                    "      \"product_name\":\"商品名\",\n" +
//                    "      \"product_num\":12,\n" +
//                    "      \"total_fee\":200.12,\n" +
//                    "      \"itempayment\":200.12,\n" +
//                    "      \"discount_rate\":1.00\n" +
//                    "  }]\n" +
//                    "},\n" +
//                    "{\n" +
//                    "\"order_id\":\"12342536342\",\n" +
//                    "\"shop_name\":\"店铺名\",\n" +
//                    "\"plat_name\":\"京东\",\n" +
//                    "\"payment\":200.21,\n" +
//                    "\"created\":\"2017/12/27\",\n" +
//                    "\"status\":\"交易成功\",\n" +
//                    "\"modified\":\"2017/12/27\",\n" +
//                    "\"is_refund\":\"\",\n" +
//                    "\"itemlist\":[{\n" +
//                    "      \"product_name\":\"商品名\",\n" +
//                    "      \"product_num\":12,\n" +
//                    "      \"total_fee\":200.12,\n" +
//                    "      \"itempayment\":200.12,\n" +
//                    "      \"discount_rate\":1.00\n" +
//                    "  }]\n" +
//                    "}\n" +
//                    "]\n";
//        }else if(url.contains("sendValidateCode")){
//            str = "{\n" +
//                    "        \"status\": \"发送成功\",\n" +
//                    "         \"validateCode\":\"123465\"\n" +
//                    "    }";
//        }else if(url.contains("queryInvalidPointByOpenId")){
//            str = "{\n" +
//                    "        \"invalid_data\": \"2017-11-13\",\n" +
//                    "        \"invalid_point\":\"2000\" \n" +
//                    "    }\n";
//        }else if(url.contains("queryUserInfoForWechat")){
//            str = "[{\n" +
//                    "    \"subscribe\": 1, \n" +
//                    "    \"openid\": \"o6_bmjrPTlm6_2sgVt7hMZOPfL2M\", \n" +
//                    "    \"nickname\": \"Band\", \n" +
//                    "    \"sex\": 1, \n" +
//                    "    \"language\": \"zh_CN\", \n" +
//                    "    \"city\": \"广州\", \n" +
//                    "    \"province\": \"广东\", \n" +
//                    "    \"country\": \"中国\", \n" +
//                    "    \"headimgurl\":\"http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0\",\n" +
//                    "    \"subscribe_time\": 1382694957,\n" +
//                    "    \"unionid\": \"o6_bmasdasdsad6_2sgVt7hMZOPfL\",\n" +
//                    "    \"remark\": \"\",\n" +
//                    "    \"groupid\": 0,\n" +
//                    "    \"tagid_list\":[128,2]\n" +
//                    "\n" +
//                    "}\n" +
//                    "]\n";
//        }else if(url.contains("fetchUserToken")){
//            str = "[{ \"access_token\":\"ACCESS_TOKEN\",\n" +
//                    "\"expires_in\":7200,\n" +
//                    "\"refresh_token\":\"REFRESH_TOKEN\",\n" +
//                    "\"openid\":\"OPENID\",\n" +
//                    "\"scope\":\"SCOPE\" }\n" +
//                    "]\n";
//        }
//        try{
//            return JSON.parseArray(str);
//        }catch (Exception e){
//            return JSON.parseObject(str);
//        }
        Object result = null;
        SysRequestLog log = new SysRequestLog();
        log.setReqId(StringUtils.generateUUID());
        SysUser user = (SysUser) session.getAttribute(Const.SESSION_USER);
        Properties prop = PropertiesUtils.load("config/sysconfig.properties");
        String appsecret = prop.getProperty("appsecret");
        String appkey = prop.getProperty("appkey");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        try {
            StringBuffer sb = new StringBuffer();
            String uri = data.getString("url");
            sb.append(uri);
//            HttpPost httpPost = new HttpPost(uri);
//            List<NameValuePair> nvps = new ArrayList<>();
            Set<String> sets = data.keySet();
//            String timestamp = System.currentTimeMillis() + "";
            String timestamp = String.valueOf(new Date().getTime());
//            nvps.add(new BasicNameValuePair("timestamp", timestamp));
            sb.append("?timestamp=" + timestamp);
            String sign = StringUtils.createSign(timestamp, appsecret);
//            nvps.add(new BasicNameValuePair("sign", sign));
            sb.append("&sign=" + sign);
//            nvps.add(new BasicNameValuePair("appkey", appkey));
            sb.append("&appkey=" + appkey);
            for (String key : sets) {
                if ("url".equals(key)) {
                    continue;
                }
                String v = data.get(key) + "";
//                nvps.add(new BasicNameValuePair(key, v));
                sb.append("&" + key + "=" + v);
            }
//            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
//            httpPost.addHeader("Content-type", "application/x-www-form-urlencoded");
//            httpPost.addHeader("User-Agent","Mozilla/5.0");
//            httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            log.setReqMessage(sb.toString());

            HttpGet httpGet = new HttpGet(sb.toString());
            httpGet.addHeader("User-Agent", "Mozilla/5.0");

            String begin = formatter.format(new Date());
            log.setReqTime(formatter.parse(begin));
            CloseableHttpClient client = HttpClients.createDefault();
//            CloseableHttpResponse response = client.execute(httpPost);
            CloseableHttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String end = formatter.format(new Date());
            log.setRespTime(formatter.parse(end));
            if (entity != null) {
                String body = EntityUtils.toString(entity, "utf-8");
                JSONObject jsonObject = JSON.parseObject(body);
                log.setRespMessage(jsonObject + "");
                if ("SUCCESS".equals(jsonObject.get("code"))) {
                    result = jsonObject.get("jsondata");
                } else {
                    throw new RuntimeException(jsonObject.get("message") + "");
                }
            }
            EntityUtils.consume(entity);
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            String respMessage = log.getRespMessage();
            do {
                log.setId(null);
                if (respMessage != null) {
                    log.setRespMessage(respMessage.substring(0, Math.min(respMessage.length(), 5000)));
                    respMessage = respMessage.substring(log.getRespMessage().length(), respMessage.length());
                }
                remoteInterfaceService.saveRequestLog(log, user);
            } while (respMessage != null && respMessage.length() > 0);
        }
        return result;
    }
}
