package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Cache;

import java.util.UUID;

/**
 * mapper-executor
 * Author: Vasylenko Oleksii
 * Date: 04.08.2024
 */
@Data
@Entity
@Builder
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "drugs")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Указываем, что включаем только явно указанные поля
public class DrugEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    UUID id;

    @Column(name = "user_login")
    String userLogin;

    @Column(name = "drug_id")
    String drugId;
    
    @Column(name = "drug_name")
    @EqualsAndHashCode.Include
    String drugName;

    @Column(name = "drug_producer")
    @EqualsAndHashCode.Include
    String drugProducer;

    @Column(name = "morion_id")
    @EqualsAndHashCode.Include
    String morionId;

    @Column(name = "optima_id")
    @EqualsAndHashCode.Include
    String optimaId;

    @Column(name = "barcode")
    @EqualsAndHashCode.Include
    String barcode;

    @Column(name = "home")
    @EqualsAndHashCode.Include
    String home;

    @Column(name = "pfactor")
    @EqualsAndHashCode.Include
    Integer pfactor;
}
