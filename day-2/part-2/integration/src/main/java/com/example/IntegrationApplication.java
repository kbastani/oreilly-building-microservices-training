package com.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.dsl.file.Files;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.transformer.FileToStringTransformer;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@SpringBootApplication
public class IntegrationApplication {

	public static final String OREILLY_FILES = "oreillyfiles";

	private final Log log = LogFactory.getLog(getClass());

	@Bean
	IntegrationFlow files(Channels channels) {

		FileReadingMessageSource fileSpec =
				Files
						.inboundAdapter(new File("/Users/jlong/Desktop/in"))
						.autoCreateDirectory(true)
						.get();

		return IntegrationFlows
				.from(fileSpec, spec -> spec.poller(poller -> poller.fixedRate(1000)))
				.transform(new FileToStringTransformer())
				.channel(channels.logger())
				.get();
	}

	@RestController
	public static class MessageEndpointRestController {

		private MessageChannel messageChannel;

		public MessageEndpointRestController(Channels channels) {
			messageChannel = channels.logger();
		}
		@GetMapping("/hi/{name}")
		public void hi(@PathVariable String name) {

			Message<String> stringMessage = MessageBuilder
					.withPayload(name)
					.build();

			this.messageChannel.send(stringMessage);

		}


	}

	@Bean
	IntegrationFlow uppercaseFlow(Channels channels, AmqpTemplate amqpTemplate) {
		return IntegrationFlows
				.from(channels.logger())
				.transform((GenericTransformer<String, String>) str -> {
					String uc = ("" + str).toUpperCase();
					log.info("(contents)! " + uc);
					return uc;
				})
				.handleWithAdapter(adapters ->
						adapters.amqp(amqpTemplate)
								.exchangeName(OREILLY_FILES)
								.routingKey(OREILLY_FILES)
				)
				.get();
	}

	@Configuration
	public static class AmqpConfiguration {


		@Bean
		Exchange exchange() {
			return ExchangeBuilder.directExchange(OREILLY_FILES).build();
		}

		@Bean
		Queue queue() {
			return QueueBuilder.durable(OREILLY_FILES).build();
		}

		@Bean
		Binding binding() {
			return BindingBuilder
					.bind(queue())
					.to(exchange())
					.with(OREILLY_FILES)
					.noargs();
		}

		@Autowired
		public void config(AmqpAdmin amqpAdmin) {
			amqpAdmin.declareExchange(exchange());
			amqpAdmin.declareQueue(queue());
			amqpAdmin.declareBinding(binding());
		}
	}

	@Configuration
	public static class Channels {

		@Bean
		MessageChannel logger() {
			return MessageChannels
					.direct()
					.get();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(IntegrationApplication.class, args);
	}
}
