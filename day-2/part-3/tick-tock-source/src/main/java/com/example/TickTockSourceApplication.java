package com.example;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Objects;

@SpringBootApplication
@EnableBinding(Source.class)
public class TickTockSourceApplication {

    private final Source source;
    private final Logger log = Logger.getLogger(this.getClass());
    private static String theClock = "tick";

    public static void main(String[] args) {
        SpringApplication.run(TickTockSourceApplication.class, args);
    }

    public TickTockSourceApplication(Source source) {
        this.source = source;
    }

    @Scheduled(fixedRate = 2000L)
    public void tickOrTock() {

        source.output().send(MessageBuilder
                .withPayload(theClock)
                .build());

        log.info(theClock);

        theClock = Objects.equals(theClock, "tock") ? "tick" : "tock";
    }

}
