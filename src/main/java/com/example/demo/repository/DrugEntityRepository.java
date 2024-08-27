package com.example.demo.repository;

import com.example.demo.model.DrugEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * mapper-executor
 * Author: Vasylenko Oleksii
 * Date: 04.08.2024
 */
@Repository
public interface DrugEntityRepository extends JpaRepository<DrugEntity, UUID> {
    List<DrugEntity> findByUserLogin(String userLogin);
    Optional<DrugEntity> findByUserLoginAndDrugId(String userLogin, String drugId);
}
