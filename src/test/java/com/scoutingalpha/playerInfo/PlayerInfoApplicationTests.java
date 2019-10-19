package com.scoutingalpha.playerInfo;

//import org.junit.jupiter.api.Test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.cloud.discovery.enabled = false"})
public class PlayerInfoApplicationTests {

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    ReactiveJwtDecoder jwtDecoder;

    @Test
    public void testAddPlayer() {
        Player player = new Player(UUID.randomUUID(), "Kjell", "Dhalin", LocalDate.of(1963, Month.FEBRUARY, 2));

        Jwt jwt = jwt();
        when(this.jwtDecoder.decode(anyString())).thenReturn(Mono.just(jwt));

        webTestClient.post().uri("/players")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .headers(addJwt(jwt))
                .body(Mono.just(player), Player.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.firstName").isEqualTo("Kjell");
    }

    @Test
    public void testGetAllPlayers() {
        Jwt jwt = jwt();
        when(this.jwtDecoder.decode(anyString())).thenReturn(Mono.just(jwt));

        webTestClient.get().uri("/players")
                .accept(MediaType.APPLICATION_JSON)
                .headers(addJwt(jwt))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Player.class);
    }

    @Test
    public void testDeletePlayer() {
        Jwt jwt = jwt();
        when(this.jwtDecoder.decode(anyString())).thenReturn(Mono.just(jwt));

        Player player = new Player(UUID.randomUUID(), "Kjell", "Dhalin", LocalDate.of(1963, Month.FEBRUARY, 2));
        player = playerRepository.save(player).block();
        webTestClient.delete()
                .uri("/players/{id}", Collections.singletonMap("id", player.getId()))
                .headers(addJwt(jwt))
                .exchange()
                .expectStatus().isOk();
    }

    private Consumer<HttpHeaders> addJwt(Jwt jwt) {
        return httpHeaders -> httpHeaders.setBearerAuth(jwt.getTokenValue());
    }


    private Jwt jwt() {
        return new Jwt("token", null, null, Map.of("alg", "non"), Map.of("sub", "woot"));
    }
}
