package com.affordmed.ingredientanalyzer.service;

import com.affordmed.ingredientanalyzer.entity.IngredientUsage;
import com.affordmed.ingredientanalyzer.repository.IngredientUsageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientUsageService {

    private final IngredientUsageRepository ingredientUsageRepository;

    public IngredientUsageService(
            IngredientUsageRepository ingredientUsageRepository) {
        this.ingredientUsageRepository = ingredientUsageRepository;
    }

    public IngredientUsage saveUsage(IngredientUsage usage) {
        return ingredientUsageRepository.save(usage);
    }

    public List<IngredientUsage> getByIngredient(Long ingredientId) {
        return ingredientUsageRepository.findByIngredientId(ingredientId);
    }

    public List<IngredientUsage> getByIngredientAndCategory(
            Long ingredientId,
            String category) {

        return ingredientUsageRepository
                .findByIngredientIdAndCategory(ingredientId, category);
    }
}