package org.gwhere.utils;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.UUID;

/**
 * Created by jiangtao on 15-7-9.
 */
public class StringUtils {

    public static boolean hasText(CharSequence str) {
        if (!hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public static boolean hasText(String str) {
        return hasText((CharSequence) str);
    }

    public static boolean hasLength(CharSequence str) {
        return (str != null && str.length() > 0);
    }

    public static boolean hasLength(String str) {
        return hasLength((CharSequence) str);
    }

    public static String GetRandomString(int Len) {

        String[] baseString = {
                "0", "1", "2", "3", "4",
                "5", "6", "7", "8", "9",
                "A", "B", "C", "D", "E",
                "F", "G", "H", "I", "J",
                "K", "L", "M", "N", "O",
                "P", "Q", "R", "S", "T",
                "U", "V", "W", "X", "Y", "Z"};
        Random random = new Random();
        int length = baseString.length;
        String randomString = "";
        for (int i = 0; i < length; i++) {
            randomString += baseString[random.nextInt(length)];
        }
        random = new Random(System.currentTimeMillis());
        String resultStr = "";
        for (int i = 0; i < Len; i++) {
            resultStr += randomString.charAt(random.nextInt(randomString.length() - 1));
        }
        return resultStr;
    }

    /**
     * 生成签名算法
     *
     * @param timestamp 当前时间的时间戳（毫秒）
     * @return 加密签名
     * @throws UnsupportedEncodingException
     */
    public static String createSign(String timestamp, String appsecret) throws UnsupportedEncodingException {
        String str = timestamp + "&" + appsecret;
        String urlEncode = java.net.URLEncoder.encode(str.toLowerCase(), "utf-8").toLowerCase();
        String strMd5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(urlEncode);
        return strMd5.toLowerCase();
    }

    /**
     * 生成一个UUID
     *
     * @return
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 获得指定数目的UUID
     *
     * @param number int 需要获得的UUID数量
     * @return String[] UUID数组
     */
    public static String[] getUUID(int number) {
        if (number < 1) {
            return null;
        }
        String[] retArray = new String[number];
        for (int i = 0; i < number; i++) {
            retArray[i] = generateUUID();
        }
        return retArray;
    }
}
