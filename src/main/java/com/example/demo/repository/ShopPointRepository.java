package com.example.demo.repository;

import com.example.demo.model.ShopPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 06.08.2024
 */
@Repository
public interface ShopPointRepository extends JpaRepository<ShopPoint, Long> {
    List<ShopPoint> findByMorionCorpId(String morionCorpId);
    Optional<ShopPoint> findByShopExtId(String shopExtId);
}
