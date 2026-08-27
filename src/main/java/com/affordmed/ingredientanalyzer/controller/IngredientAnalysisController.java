package com.affordmed.ingredientanalyzer.controller;

import com.affordmed.ingredientanalyzer.dto.IngredientAnalysisResult;
import com.affordmed.ingredientanalyzer.service.IngredientAnalysisService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analyze")
public class IngredientAnalysisController {

    private final IngredientAnalysisService ingredientAnalysisService;

    public IngredientAnalysisController(
            IngredientAnalysisService ingredientAnalysisService) {

        this.ingredientAnalysisService = ingredientAnalysisService;
    }

    @GetMapping("/ingredient")
    public Object analyzeOneIngredient(
            @RequestParam String name,
            @RequestParam String category) {

        return ingredientAnalysisService
                .analyzeIngredient(name, category);
    }

    @PostMapping("/ingredients")
    public List<IngredientAnalysisResult> analyzeIngredients(
            @RequestBody List<String> ingredients,
            @RequestParam String category) {

        return ingredientAnalysisService
                .analyzeIngredients(ingredients, category);
    }
}