package org.gwhere.service;

import org.gwhere.model.SysRequestLog;
import org.gwhere.model.SysUser;

public interface RemoteInterfaceService {

    void saveRequestLog(SysRequestLog log, SysUser user);

}
