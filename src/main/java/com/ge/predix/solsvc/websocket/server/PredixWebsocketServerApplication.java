package com.ge.predix.solsvc.websocket.server;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.web.context.support.StandardServletEnvironment;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/**
 * 
 * @author predix -
 */
@SpringBootApplication
@EnableWebSocket
@ComponentScan(basePackages={"com.ge.predix.solsvc.websocket.server","com.ge.predix.solsvc.restclient","com.ge.predix.solsvc.websocket"})
public class PredixWebsocketServerApplication{
	private static Logger log = LoggerFactory.getLogger(PredixWebsocketServerApplication.class);
	
	/**
	 * @param args -
	 */
	@SuppressWarnings("nls")
    public static void main(String[] args) {
		// SpringApplication.run(PredixWebsocketServerApplication.class, args);
		SpringApplication app = new SpringApplication(PredixWebsocketServerApplication.class);
		ConfigurableApplicationContext ctx = app.run(args);

		log.info("Let's inspect the properties provided by Spring Boot:");
		MutablePropertySources propertySources = ((StandardServletEnvironment) ctx.getEnvironment())
				.getPropertySources();
		Iterator<org.springframework.core.env.PropertySource<?>> iterator = propertySources.iterator();
		while (iterator.hasNext()) {
			Object propertySourceObject = iterator.next();
			if (propertySourceObject instanceof org.springframework.core.env.PropertySource) {
				org.springframework.core.env.PropertySource<?> propertySource = (org.springframework.core.env.PropertySource<?>) propertySourceObject;
				log.info("propertySource=" + propertySource.getName() + " values=" + propertySource.getSource()
						+ "class=" + propertySource.getClass());
			}
		}

		log.info("Let's inspect the profiles provided by Spring Boot:");
		String profiles[] = ctx.getEnvironment().getActiveProfiles();
		for (int i = 0; i < profiles.length; i++)
			log.info("profile=" + profiles[i]);
		
		log.info("Let's inspect the beans provided by Spring Boot:");
		String beanNames[] = ctx.getBeanDefinitionNames();
		for (String beanName:beanNames)
			log.info(beanName+" : "+ctx.getBean(beanName));
		
	}
}
