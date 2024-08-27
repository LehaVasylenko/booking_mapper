package com.example.demo.service;

import com.example.demo.config.PopOrderProperties;
import com.example.demo.model.api.BookingObject;
import com.example.demo.model.api.order_geoapteka.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

/**
 * demo
 * Author: Vasylenko Oleksii
 * Date: 08.08.2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingService {
    ObjectMapper mapper = new ObjectMapper();
    RestTemplate restTemplate;
    PopOrderProperties properties;

    @Async
    public CompletableFuture<Order[]> askBooking(BookingObject body) {
        return CompletableFuture.supplyAsync(() -> {
            String url = properties.getUrl() + properties.getPop();
            try {
                String requestBodyJson = mapper.writeValueAsString(body.getPayload());
                log.info(requestBodyJson);
                HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, getHttpHeaders(body.getMorionLogin(), body.getMorionKey()));
                log.info(entity.toString());
                ResponseEntity<Order[]> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, Order[].class);
                log.info(exchange.toString());
                if (exchange.getBody() != null) {
                    for (Order order : exchange.getBody()) {
                        log.info(order.toString());
                    }
                }
                return exchange.getBody();
            } catch (HttpClientErrorException ex) {
                log.error("{}: {}", ex.getStatusCode(), ex.getMessage());
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
            }
            return new Order[0];
        });
    }

    @Async
    public CompletableFuture<String> tellBooking(Order order, BookingObject body) {
        return CompletableFuture.supplyAsync(() -> {
            String url = properties.getUrl() + properties.getUpd();
            try {
                String requestBodyJson = mapper.writeValueAsString(order);

                HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, getHttpHeaders(body.getMorionLogin(), body.getMorionKey()));
                log.info(entity.toString());

                ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
                log.info(exchange.toString());
                return exchange.getStatusCode().toString();
            } catch (HttpClientErrorException ex) {
                log.error("{}: {}", ex.getStatusCode(), ex.getMessage());
                return ex.getStatusCode() + ": " + ex.getMessage();
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
                return e.getMessage();
            }
        });
    }

    private HttpHeaders getHttpHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", getAuthHeader(username, password));
        headers.set("User-Agent", "Tabletki Mapper");
        return headers;
    }

    private String getAuthHeader(String username, String password) {
        String auth = new StringBuilder().append(username).append(":").append(password).toString();
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedAuth);
    }
}

