package com.scoutingalpha.playerInfo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

// https://developer.okta.com/blog/2019/08/28/reactive-microservices-spring-cloud-gateway

@RestController
public class PlayerController {
    private PlayerRepository playerRepository;

    public PlayerController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @PostMapping("/players")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Player> addPlayer(@RequestBody Player player) {
        return playerRepository.save(player);
    }

    @GetMapping("/players")
    public Flux<Player> getPlayers(){
        return playerRepository.findAll();
    }

    @DeleteMapping("/players/{id}")
    public Mono<ResponseEntity<Void>> deletePlayer(@PathVariable("id") UUID id) {
        return playerRepository.findById(id)
                .flatMap(player -> playerRepository.delete(player)
                        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
                )
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
