package org.gwhere.service;

import org.gwhere.model.SysUser;
import org.gwhere.model.SysUserAction;

import java.util.List;

public interface ActionService {

    void saveUserAction(List<SysUserAction> actions, SysUser user);

}
