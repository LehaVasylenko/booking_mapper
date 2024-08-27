package com.example.demo.model.api.order_geoapteka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

   @JsonProperty("id_shop")
   String idShop;

   @JsonProperty("ext_id_shop")
   String extidShop;

   String phone;

   Boolean test;

   Long timestamp;

   String agent;

   @JsonProperty("id_order")
   String idOrder;

   @JsonProperty("ext_id_order")
   String idExtOrder;

   String shipping;

   String state;

   String reason;

   List<OrderPreps> data;
}

