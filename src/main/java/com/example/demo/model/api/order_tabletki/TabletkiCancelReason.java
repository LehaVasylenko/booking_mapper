package com.example.demo.model.api.order_tabletki;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * demo
 * Author: Vasylenko Oleksii
 * Date: 12.08.2024
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TabletkiCancelReason {
    String id;

    @JsonProperty("id_CancelReason")
    float idCancelReason;

    List<RowOutgoing> rows;
}
