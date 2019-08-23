package com.miracle.jms.config;

import javax.jms.MessageListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.transaction.PlatformTransactionManager;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.mq.jms.MQTopicConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import com.miracle.jms.listner.PrintListener;

@Configuration
public class JmsConfig {

	@Value("${servers.mq.host}")
	private String host;
	@Value("${servers.mq.port}")
	private Integer port;
	@Value("${servers.mq.queue-manager}")
	private String queueManager;
	@Value("${servers.mq.channel}")
	private String channel;
	@Value("${servers.mq.queue}")
	private String queue;
	@Value("${servers.mq.timeout}")
	private long timeout;
	@Value("${servers.mq.username}")
	private String userName;
	@Value("${servers.mq.password}")
	private String password;

	@Autowired
	PrintListener printListener;

	@Bean
	public MQQueueConnectionFactory mqQueueConnectionFactory() {
		MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
		mqQueueConnectionFactory.setHostName(host);
		try {
			mqQueueConnectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
			mqQueueConnectionFactory.setCCSID(1208);
			mqQueueConnectionFactory.setChannel(channel);
			mqQueueConnectionFactory.setPort(port);
			mqQueueConnectionFactory.setQueueManager(queueManager);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mqQueueConnectionFactory;
	}

	@Bean
	UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter(
			MQQueueConnectionFactory mqQueueConnectionFactory) {
		UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter = new UserCredentialsConnectionFactoryAdapter();
		userCredentialsConnectionFactoryAdapter.setUsername(userName);
		userCredentialsConnectionFactoryAdapter.setPassword(password);
		userCredentialsConnectionFactoryAdapter.setTargetConnectionFactory(mqQueueConnectionFactory);
		return userCredentialsConnectionFactoryAdapter;
	}

	@Bean
	@Primary
	public CachingConnectionFactory cachingConnectionFactory(
			UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter) {
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
		cachingConnectionFactory.setTargetConnectionFactory(userCredentialsConnectionFactoryAdapter);
		cachingConnectionFactory.setSessionCacheSize(500);
		cachingConnectionFactory.setReconnectOnException(true);
		return cachingConnectionFactory;
	}

	@Bean
	public PlatformTransactionManager jmsTransactionManager(CachingConnectionFactory cachingConnectionFactory) {
		JmsTransactionManager jmsTransactionManager = new JmsTransactionManager();
		jmsTransactionManager.setConnectionFactory(cachingConnectionFactory);
		return jmsTransactionManager;
	}

	@Bean
	public JmsTemplate jmsOperations(CachingConnectionFactory cachingConnectionFactory) {
		JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory);
		jmsTemplate.setReceiveTimeout(timeout);
		return jmsTemplate;
	}

	@Bean
	public SimpleMessageListenerContainer queueContainer(CachingConnectionFactory cachingConnectionFactory) {
		MessageListener listener = printListener;
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(cachingConnectionFactory);
		container.setDestinationName(queue);
		container.setMessageListener(listener);
		container.start();
		return container;
	}
}
