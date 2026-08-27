package com.affordmed.ingredientanalyzer.controller;

import com.affordmed.ingredientanalyzer.dto.IngredientKnowledgeResult;
import com.affordmed.ingredientanalyzer.service.IngredientKnowledgeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/knowledge")
public class IngredientKnowledgeController {

    private final IngredientKnowledgeService ingredientKnowledgeService;

    public IngredientKnowledgeController(
            IngredientKnowledgeService ingredientKnowledgeService) {

        this.ingredientKnowledgeService = ingredientKnowledgeService;
    }

    @GetMapping("/ingredient")
    public IngredientKnowledgeResult analyzeIngredient(
            @RequestParam String name,
            @RequestParam String category) {

        return ingredientKnowledgeService
                .analyzeIngredientWithAI(name, category);
    }
}