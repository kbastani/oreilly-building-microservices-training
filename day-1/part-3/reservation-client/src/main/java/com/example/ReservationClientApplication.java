package com.example;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.hateoas.Resources;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@EnableResourceServer
@EnableCircuitBreaker
@EnableFeignClients
@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication
public class ReservationClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationClientApplication.class, args);
	}
}

@RestController
@RequestMapping("/reservations")
class ReservationsApiAdapterRestController {

	private final ReservationReader r;

	public ReservationsApiAdapterRestController(ReservationReader r) {
		this.r = r;
	}

	public Collection<String> fallback() {
		return new ArrayList<>();
	}

	@GetMapping("/names")
	@HystrixCommand(fallbackMethod = "fallback")
	public Collection<String> names() {
		return r.read()
				.getContent()
				.stream()
				.map(Reservation::getReservationName)
				.collect(Collectors.toList());
	}

}


@FeignClient("reservation-service")
interface ReservationReader {

	@RequestMapping(method = RequestMethod.GET, value = "/reservations")
	Resources<Reservation> read();
}

class Reservation {
	private String reservationName;

	public String getReservationName() {
		return reservationName;
	}
}