package com.example.demo.model.api.order_tabletki;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RowOutgoing {
    String goodsCode;
    String goodsName;
    String goodsProducer;
    Float qty;
    Float price;
}
