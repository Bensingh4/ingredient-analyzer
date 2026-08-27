package com.affordmed.ingredientanalyzer.controller;

import com.affordmed.ingredientanalyzer.entity.Ingredient;
import com.affordmed.ingredientanalyzer.service.IngredientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @PostMapping
    public Ingredient createIngredient(@RequestBody Ingredient ingredient) {
        return ingredientService.saveIngredient(ingredient);
    }

    @GetMapping
    public List<Ingredient> getAllIngredients() {
        return ingredientService.getAllIngredients();
    }

    @GetMapping("/search")
    public Object findIngredient(@RequestParam String name) {
        return ingredientService.findByName(name);
    }
}