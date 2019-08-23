package com.miracle.swagger.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
public class SwaggerHomeController {

	private String Home_Controller = "redirect:swagger-ui.html";

	@RequestMapping(value = "/")
	public String homeIndex() {
		return Home_Controller;

	}
}
