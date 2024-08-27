package com.example.demo.repository;


import com.example.demo.model.order.PrepsInOrderDb;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrepsInOrderRepository extends JpaRepository<PrepsInOrderDb, Long> {
}
