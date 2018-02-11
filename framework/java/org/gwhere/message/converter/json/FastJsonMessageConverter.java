package org.gwhere.message.converter.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.gwhere.message.converter.AbstractMessageConverter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by jiangtao on 15-7-13.
 */
public class FastJsonMessageConverter extends AbstractMessageConverter<Object, Object, InputStream, OutputStream> {

    @Override
    protected Object readInternal(Class clazz, InputStream inputMessage) throws IOException {
        String jsonStr = getInputStreamString(inputMessage);

        try {
            if (clazz == String.class) {
                return jsonStr;
            } else if (clazz == byte[].class) {
                return jsonStr.getBytes("UTF-8");
            } else if (clazz.isArray()) {
                return JSONArray.parseArray(jsonStr, clazz);
            } else {
                return JSON.parseObject(jsonStr, clazz);
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.error("解析json异常:\n" + jsonStr, e);
            }
            throw e;
        }

    }

    @Override
    protected void writeInternal(Object o, OutputStream outputMessage) throws IOException {
        if (o instanceof byte[]) {
            outputMessage.write((byte[]) o);
        } else {
            outputMessage.write(JSON.toJSONString(o).getBytes("UTF-8"));
        }
    }

    @Override
    public boolean canRead(Class<?> clazz) {
        return true;
    }

    @Override
    public boolean canWrite(Class<?> clazz) {
        return true;
    }

    private String getInputStreamString(InputStream inputStream) {
        String jsonStr = "";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, len);
            }
            jsonStr = new String(out.toByteArray(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }
}
