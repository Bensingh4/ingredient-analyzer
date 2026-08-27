package com.affordmed.ingredientanalyzer.controller;

import com.affordmed.ingredientanalyzer.ai.AiService;
import com.affordmed.ingredientanalyzer.dto.ProductScanResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/scan")
public class ProductScanController {

    private final AiService aiService;

    public ProductScanController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/image")
    public ProductScanResult scanProduct(
            @RequestParam("image") MultipartFile image) throws Exception {

        return aiService.analyzeProductImage(image);
    }
}