package org.gwhere.utils;

import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;

/**
 * Created by jiangtao on 16/6/23.
 */
public class MD5 {

    public static String getMD5(CharSequence s) {
        try {
            byte[] bytes = s.toString().getBytes("UTF-8");
            bytes = DigestUtils.md5Digest(bytes);
            return StringUtils.bytesToHexString(bytes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
