package org.gwhere.message.handler.net;

import java.lang.annotation.*;
import java.util.Map;

/**
 * Created by jiangtao on 15-7-15.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RequestMsgDesc {

    String url();

    boolean isDoOutput() default true;

    boolean isDoInput() default true;

    String requestMethod() default "POST";

    Class requestDataType() default Object.class;

    Class responseDataType() default Map.class;

}
