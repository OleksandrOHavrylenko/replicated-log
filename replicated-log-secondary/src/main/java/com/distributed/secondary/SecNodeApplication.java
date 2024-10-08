package com.distributed.secondary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SecNodeApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(SecNodeApplication.class, args);

		ServerGrpc serverGrpc = applicationContext.getBean("serverGrpc", ServerGrpc.class);
		serverGrpc.start();
	}
}
