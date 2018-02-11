package org.gwhere.message.handler.net;

import java.util.HashMap;

/**
 * Created by jiangtao on 16/2/26.
 */
public class FormData extends HashMap {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Object key : keySet()) {
            sb.append(key.toString()).append("=").append(get(key).toString()).append("&");
        }
        return sb.substring(0, sb.length() - 1);
    }
}
