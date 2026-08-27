package com.affordmed.ingredientanalyzer.controller;

import com.affordmed.ingredientanalyzer.openfoodfacts.OpenFoodFactsService;
import org.springframework.web.bind.annotation.*;
import com.affordmed.ingredientanalyzer.dto.OpenFoodFactsResponse;
@RestController
@RequestMapping("/api/food")
public class OpenFoodFactsController {

    private final OpenFoodFactsService openFoodFactsService;

    public OpenFoodFactsController(OpenFoodFactsService openFoodFactsService) {
        this.openFoodFactsService = openFoodFactsService;
    }

    @GetMapping("/{barcode}")
    public OpenFoodFactsResponse getProduct(@PathVariable String barcode) {
        return openFoodFactsService.getProductByBarcode(barcode);
    }
}