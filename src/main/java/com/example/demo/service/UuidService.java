package com.example.demo.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * demo
 * Author: Vasylenko Oleksii
 * Date: 12.08.2024
 */
@Slf4j
@Service
@EnableScheduling
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UuidService {

    Set<UUID> uuidSet = new HashSet<>();

    @Async
    public CompletableFuture<UUID> generateUniqueUUID() {
        UUID newUUID;
        do {
            newUUID = UUID.randomUUID();
        } while (!uuidSet.add(newUUID)); // add() вернет false, если UUID уже существует

        return CompletableFuture.completedFuture(newUUID);
    }

//    @Scheduled(cron = "0 0 0 * * SUN")
//    public void clearUuidSet() {
//        int size = uuidSet.size();
//        uuidSet.clear();
//        log.info("Cleaned {} UUID", size);
//    }
}
