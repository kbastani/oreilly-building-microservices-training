package com.example;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;

@EnableTask
@SpringBootApplication
public class MySimpleTaskApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(MySimpleTaskApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments applicationArguments) throws Exception {
		System.out.println("Hello world!");
		Thread.sleep(10 * 1000);
	}
}
