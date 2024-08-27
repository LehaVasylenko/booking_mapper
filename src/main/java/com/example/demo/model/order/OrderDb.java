package com.example.demo.model.order;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@ToString(exclude = {"states"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDb{
    @Id
    @Column(name = "tabletki_order_id")
    UUID tabletkiOrderId;

    @Column(name = "username")
    String username;

    @Column(name = "morion_order_id")
    String morionOrderId;

    @Column(name = "shop_order_id")
    String shopExtOrderId;

    @Column(name = "shop_id")
    String shopId;

    @Column(name = "shop_ext_id")
    String shopExtId;

    @Column(name = "phone")
    String phone;

    @Column(name = "agent")
    String agent;

    @Column(name = "time")
    LocalDateTime timestamp;

    @Column(name = "shipping")
    String shipping;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    List<State> states;
}
