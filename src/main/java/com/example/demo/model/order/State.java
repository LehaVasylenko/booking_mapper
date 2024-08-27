package com.example.demo.model.order;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "states")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@ToString(exclude = {"order", "prepsInOrder"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class State extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "tabletki_order_id", nullable = false)
    OrderDb order;

    @Column(name = "time")
    LocalDateTime time;

    @Column(name = "state")
    String state;

    @Column(name = "cancel_reason")
    String reason;

    @OneToMany(mappedBy = "state", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    List<PrepsInOrderDb> prepsInOrder;
}
