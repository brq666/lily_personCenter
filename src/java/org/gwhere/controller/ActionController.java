package org.gwhere.controller;

import org.gwhere.constant.Const;
import org.gwhere.model.*;
import org.gwhere.service.ActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ac")
public class ActionController {
	@Autowired
	private ActionService actionService;

	//用户行为
	@RequestMapping("/saveUserAction")
	@ResponseBody
	public void saveUserAction(@RequestBody List<SysUserAction> actions,HttpSession session) throws Exception {
		actionService.saveUserAction(actions, (SysUser) session.getAttribute(Const.SESSION_USER));
	}
}
