package com.example.demo.model.api.order_tabletki;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * demo
 * Author: Vasylenko Oleksii
 * Date: 08.08.2024
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TabletkiNewOrder {
    String id;
    Integer code;
    Float statusID;
    String dateTimeCreated;
    String customer;
    String customerPhone;
    String customerEmail;
    String comment;
    String branchID;
    String externalNmb;
    String docAdditionalInfo;
    String customerAdditionalInfo;
    String reserveSource;
    String cancelReason;
    List<RowOutgoing> rows;

}
