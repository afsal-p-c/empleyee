package com.example.employeemanagementsystem.repository;

import com.example.employeemanagementsystem.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade,Long> {

    Optional<Grade> findByName(String grade);

    boolean existsByName(String gradeName);
}
