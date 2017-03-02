package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.stream.Stream;

@EnableDiscoveryClient
@SpringBootApplication
public class ReservationServiceApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ReservationServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Stream.of("Madhura", "Jennifer",
				"Kenny", "Josh",
				"Dave", "Spencer").forEach(name -> reservationRepository.save(new Reservation(name)));

		reservationRepository.findAll().forEach(System.out::println);

	}

	private final ReservationRepository reservationRepository;

	ReservationServiceApplication(ReservationRepository reservationRepository) {
		this.reservationRepository = reservationRepository;
	}
}

@RestController
@RefreshScope
class MessageRestController {

	private final String value;

	MessageRestController(@Value("${message}") String value) {
		this.value = value;
	}

	@GetMapping("/message")
	String read() {
		return this.value;
	}
}

@RepositoryRestResource
interface ReservationRepository extends JpaRepository<Reservation, Long> {

}

@Entity
class Reservation {

	@Id
	@GeneratedValue
	private Long id;

	private String reservationName; // reservation_name

	@Override
	public String toString() {
		return "Reservation{" +
				"id=" + id +
				", reservationName='" + reservationName + '\'' +
				'}';
	}

	Reservation() {// why JPA why??
	}

	public Reservation(String reservationName) {

		this.reservationName = reservationName;
	}

	public Long getId() {

		return id;
	}

	public String getReservationName() {
		return reservationName;
	}
}