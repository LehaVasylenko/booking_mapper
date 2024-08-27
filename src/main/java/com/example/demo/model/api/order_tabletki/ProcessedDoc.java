package com.example.demo.model.api.order_tabletki;

/**
 * demo
 * Author: Vasylenko Oleksii
 * Date: 12.08.2024
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProcessedDoc {
    String id;
    String result;
}
