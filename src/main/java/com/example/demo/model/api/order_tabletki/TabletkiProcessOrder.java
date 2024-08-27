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
public class TabletkiProcessOrder {
    @JsonProperty("id")
    String id;

    @JsonProperty("statusID")
    float statusID;

    @JsonProperty("branchID")
    String branchID;
    String externalNmb;
    String docAdditionalInfo;
    String customerAdditionalInfo;
    String cancelReason;

    @JsonProperty("rows")
    List<RowIncoming> rows;
}
