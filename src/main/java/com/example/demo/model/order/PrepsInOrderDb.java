package com.example.demo.model.order;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "preps_in_order")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@ToString(exclude = "state")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrepsInOrderDb extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    State state;

    @Column(name = "morion_id")
    String morionId;

    @Column(name = "ext_id")
    String extId;

    @Column(name = "price")
    Float price;

    @Column(name = "quantity")
    Float quant;

    @Column(name = "drug_name")
    String drugName;

    @Column(name = "drug_producer")
    String drugProducer;

}
