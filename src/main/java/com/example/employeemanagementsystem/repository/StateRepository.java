package com.example.employeemanagementsystem.repository;

import com.example.employeemanagementsystem.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<State,Long> {
    Optional<State> findByName(String state);

    boolean existsByName(String stateName);
}
