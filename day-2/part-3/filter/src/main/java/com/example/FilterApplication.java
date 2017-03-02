package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.Filter;
import org.springframework.messaging.Message;

@SpringBootApplication
@EnableBinding(Processor.class)
public class FilterApplication {

    @Value("${filter}")
    private String filter;

    public static void main(String[] args) {
        SpringApplication.run(FilterApplication.class, args);
    }

    @Filter(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    public boolean filter(Message<?> message) {
        return filter.equals(message.getPayload());
    }
}
