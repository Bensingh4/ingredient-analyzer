package com.affordmed.ingredientanalyzer.repository;

import com.affordmed.ingredientanalyzer.entity.IngredientUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IngredientUsageRepository extends JpaRepository<IngredientUsage, Long> {

    List<IngredientUsage> findByIngredientId(Long ingredientId);

    List<IngredientUsage> findByIngredientIdAndCategory(
            Long ingredientId,
            String category
    );
}