package com.scoutingalpha.playerInfo;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.cloud.discovery.enabled = false"})
class PlayerInfoApplicationTests {

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void testAddPlayer() {
        Player player = new Player(UUID.randomUUID(), "Kjell", "Dhalin", LocalDate.of(1963, Month.FEBRUARY, 2));

		webTestClient.post().uri("/players")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(player), Player.class)
				.exchange()
				.expectStatus().isCreated()
				.expectBody()
				.jsonPath("$.id").isNotEmpty()
				.jsonPath("$.firstName").isEqualTo("Kjell");
    }

	@Test
	public void testGetAllPlayers() {
		webTestClient.get().uri("/players")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(Player.class);
	}

	@Test
	public void testDeletePlayer() {
		Player player = new Player(UUID.randomUUID(), "Kjell", "Dhalin", LocalDate.of(1963, Month.FEBRUARY, 2));
		player = playerRepository.save(player).block();
		webTestClient.delete()
				.uri("/players/{id}", Collections.singletonMap("id", player.getId()))
				.exchange()
				.expectStatus().isOk();
	}
}
