package org.gwhere.utils;

import org.gwhere.exception.ErrorCode;
import org.gwhere.exception.SystemException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {

    private final static Log logger = LogFactory.getLog(PropertiesUtils.class);

    public static Properties load(String path) {
        InputStream inputStream = PropertiesUtils.class.getClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            logger.warn("Failed to load properties from path:" + path);
            return null;
        }
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new SystemException(ErrorCode.INIT_FAILED, e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

    public static String getProperty(Properties properties, String name) {
        String value = properties.getProperty(name);
        if (!StringUtils.hasText(value)) {
            logger.warn("Failed to load property:" + name);
        }
        return value;
    }

    public static String getProperty(String path, String name) {
        Properties properties = load(path);
        return getProperty(properties, name);
    }
}
