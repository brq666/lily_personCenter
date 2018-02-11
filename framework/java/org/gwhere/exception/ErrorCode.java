package org.gwhere.exception;

/**
 * Created by jiangtao on 16/4/12.
 */
public enum ErrorCode {

    UNKNOWN("0000"),

    INIT_FAILED("0001"),

    UNSUPPORT_ROLE("0002"),

    MESSAGE_CONVERT_FAILED("0003"),

    GET_DATA_FAILED("0004"),

    MODIFY_DATA_FAILED("0005"),

    NOT_LOGIN("0006"),

    VALID_FAILED("0007");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
