package com.miracle.wtx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miracle.wtx.services.WtxTransformer;

@RestController
@RequestMapping("/jms")
public class Controller {

	@Autowired
	JmsTemplate jmsTemplate;


	@PostMapping("/sendMessage")
	public void postMessageToQueue(@RequestParam String payload) {
		System.out.println("Hello");
		jmsTemplate.convertAndSend("WTX.MAP.IN", payload);
	}
}
