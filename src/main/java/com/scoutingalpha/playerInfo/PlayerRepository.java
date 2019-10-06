package com.scoutingalpha.playerInfo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.util.UUID;

public interface PlayerRepository extends ReactiveMongoRepository<Player, UUID> {
}
