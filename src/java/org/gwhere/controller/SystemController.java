package org.gwhere.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/sys")
public class SystemController {

	//用户行为
	@RequestMapping("/p/getTime")
	@ResponseBody
	public Long getTime() throws Exception {
		return new Date().getTime();
	}
}
