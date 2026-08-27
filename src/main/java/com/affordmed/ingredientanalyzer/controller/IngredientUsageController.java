package com.affordmed.ingredientanalyzer.controller;

import com.affordmed.ingredientanalyzer.entity.IngredientUsage;
import com.affordmed.ingredientanalyzer.service.IngredientUsageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredient-usages")
public class IngredientUsageController {

    private final IngredientUsageService ingredientUsageService;

    public IngredientUsageController(
            IngredientUsageService ingredientUsageService) {
        this.ingredientUsageService = ingredientUsageService;
    }

    @PostMapping
    public IngredientUsage createUsage(
            @RequestBody IngredientUsage usage) {

        return ingredientUsageService.saveUsage(usage);
    }

    @GetMapping("/ingredient/{ingredientId}")
    public List<IngredientUsage> getByIngredient(
            @PathVariable Long ingredientId) {

        return ingredientUsageService.getByIngredient(ingredientId);
    }

    @GetMapping("/ingredient/{ingredientId}/category/{category}")
    public List<IngredientUsage> getByIngredientAndCategory(
            @PathVariable Long ingredientId,
            @PathVariable String category) {

        return ingredientUsageService
                .getByIngredientAndCategory(ingredientId, category);
    }
}