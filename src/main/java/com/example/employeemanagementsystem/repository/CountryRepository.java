package com.example.employeemanagementsystem.repository;

import com.example.employeemanagementsystem.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country,Long> {
    Optional<Country> findByName(String country);

    boolean existsByName(String countryName);
}
