package com.example.demo.repository;

import com.example.demo.model.order.State;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StateRepository extends JpaRepository<State, Long> {
}
