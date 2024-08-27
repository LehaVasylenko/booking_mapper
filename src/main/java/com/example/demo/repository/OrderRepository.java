package com.example.demo.repository;

import com.example.demo.model.order.OrderDb;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderDb, UUID> {

    @Modifying
    @Transactional
    @Query("UPDATE OrderDb o SET o.shopExtOrderId = :shopOrderId WHERE o.tabletkiOrderId = :tabletkiOrderId")
    int updateShopOrderId(@Param("shopOrderId") String shopOrderId, @Param("tabletkiOrderId") UUID tabletkiOrderId);

    @Query("SELECT o FROM OrderDb o " +
            "WHERE o.shopId = :shopId AND o.morionOrderId = :orderId")
    Optional<OrderDb> findByShopIdAndOrderId(@Param("shopId") String shopId, @Param("orderId") String orderId);

    List<OrderDb> findByShopExtIdAndUsername(String shopExtId, String username);

}
