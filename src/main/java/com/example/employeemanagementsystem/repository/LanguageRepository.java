package com.example.employeemanagementsystem.repository;

import com.example.employeemanagementsystem.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<Language,Long> {
    Optional<Language> findByName(String lang);

    boolean existsByName(String languageName);
}
