package com.example;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

@SpringBootApplication
@EnableBinding(Sink.class)
public class LogSinkApplication {

    Logger log = Logger.getLogger(this.getClass());

    public static void main(String[] args) {
        SpringApplication.run(LogSinkApplication.class, args);
    }

    @ServiceActivator(inputChannel = Sink.INPUT)
    public void process(Message<?> message) {
        log.info(message.getPayload());
    }
}
