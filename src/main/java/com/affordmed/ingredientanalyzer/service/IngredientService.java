package com.affordmed.ingredientanalyzer.service;

import com.affordmed.ingredientanalyzer.entity.Ingredient;
import com.affordmed.ingredientanalyzer.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public Ingredient saveIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Optional<Ingredient> findByName(String name) {
        return ingredientRepository.findByNameIgnoreCase(name);
    }
}