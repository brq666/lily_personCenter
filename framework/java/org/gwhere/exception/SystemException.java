package org.gwhere.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiangtao on 16/4/12.
 */
public class SystemException extends RuntimeException {

    ErrorCode errorCode;

    Map<String, Object> fieldMap;

    public SystemException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.fieldMap = new HashMap<>();
    }

    public SystemException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.fieldMap = new HashMap<>();
    }

    public SystemException(ErrorCode errorCode, Exception ex) {
        super(ex);
        this.errorCode = errorCode;
        this.fieldMap = new HashMap<>();
    }

    public SystemException(ErrorCode errorCode, String message, Exception ex) {
        super(message, ex);
        this.errorCode = errorCode;
        this.fieldMap = new HashMap<>();
    }

    public SystemException set(String fieldName, Object fieldData) {
        fieldMap.put(fieldName, fieldData);
        return this;
    }
}
