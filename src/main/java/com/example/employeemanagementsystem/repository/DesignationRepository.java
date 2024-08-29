package com.example.employeemanagementsystem.repository;

import com.example.employeemanagementsystem.entity.Designation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DesignationRepository extends JpaRepository<Designation,Long> {
    Optional<Designation> findByName(String designation);

    boolean existsByName(String designationName);
}
