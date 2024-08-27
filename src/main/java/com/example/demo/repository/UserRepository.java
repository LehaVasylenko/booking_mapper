package com.example.demo.repository;

import com.example.demo.model.UserDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * demo
 * Author: Vasylenko Oleksii
 * Date: 08.08.2024
 */
@Repository
public interface UserRepository extends JpaRepository<UserDB, Long> {
    Optional<UserDB> findByUsername(String username);
    Optional<UserDB> findByMorionCorpId(String morionCorpId);
}
