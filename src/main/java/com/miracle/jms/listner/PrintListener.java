package com.miracle.jms.listner;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.miracle.wtx.services.WtxTransformer;

@Component
public class PrintListener implements MessageListener {

	@Autowired
	WtxTransformer transformer;

	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			try {
				String payload = ((TextMessage) message).getText();
				System.out.println(payload);
				transformer.transformMap(payload);
			} catch (JMSException ex) {
				throw new RuntimeException(ex);
			}
		} else {
			throw new IllegalArgumentException("Message type must be a TextMessage");
		}
	}

}
