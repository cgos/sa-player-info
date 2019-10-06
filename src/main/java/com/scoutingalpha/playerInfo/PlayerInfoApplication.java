package com.scoutingalpha.playerInfo;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;
import java.util.UUID;

@EnableEurekaClient
@SpringBootApplication
@Slf4j
public class PlayerInfoApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlayerInfoApplication.class, args);
	}

	@Bean
	ApplicationRunner init(PlayerRepository repository) {
		Player mats = new Player(UUID.randomUUID(), "Mats", "Näslund", LocalDate.of(1959, Month.OCTOBER, 31));
		Player chris = new Player(UUID.randomUUID(), "Chris", "Chelios", LocalDate.of(1962, Month.JANUARY, 25));
		Player patrick = new Player(UUID.randomUUID(), "Patrick", "Roy", LocalDate.of(1965, Month.OCTOBER, 5));
		Set<Player> playerSet = Set.of(mats, chris, patrick);

		return args -> {
			repository
					.deleteAll()
					.thenMany(
							Flux.just(playerSet).flatMap(repository::saveAll)
					)
					.thenMany(repository.findAll())
					.subscribe(player -> log.info("saving: " + player.toString()));
		};
	}

}
