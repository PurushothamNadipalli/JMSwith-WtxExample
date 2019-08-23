package com.miracle.jms.container;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ContainerChecker {
	@Autowired
	SimpleMessageListenerContainer queueContainer;

	@Scheduled(fixedRate = 60000)
	public void reportContainerStatus() throws Exception {
		if (!queueContainer.isActive())
			throw new Exception("STATUS ERROR");
	}
}
