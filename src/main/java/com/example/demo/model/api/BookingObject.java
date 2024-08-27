package com.example.demo.model.api;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * demo
 * Author: Vasylenko Oleksii
 * Date: 08.08.2024
 */
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingObject {
    List<String> payload;
    String morionLogin;
    String morionKey;
}
