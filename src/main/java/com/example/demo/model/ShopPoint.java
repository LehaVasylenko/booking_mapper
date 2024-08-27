package com.example.demo.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 06.08.2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "shops")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShopPoint {
    @Id
    Long id;

    @Column(name = "shop_ext_id")
    String shopExtId;

    @Column(name = "shop_name")
    String shopName;

    @Column(name = "shop_head")
    String shopHead;

    @Column(name = "shop_addr")
    String shopAddr;

    @Column(name = "shop_code")
    String shopCode;

    @Column(name = "shop_morion_shop_id")
    String morionShopId;

    @Column(name = "shop_morion_corp_id")
    String morionCorpId;
}
